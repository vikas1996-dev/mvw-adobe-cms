package com.mvw.core.services.impl;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentFragmentException;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.mvw.core.constants.AppConstants;
import com.mvw.core.models.dto.TripAdvisorDto;
import com.mvw.core.services.TripAdvisorListService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionManager;
import java.util.*;

@Component(service = SaveTripAdvisorDataJcr.class, immediate = true)
public class SaveTripAdvisorDataJcr {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private Replicator replicator;

    @Reference
    private TripAdvisorListService tripAdvisorListService;

    @Reference
    private TripAdvisorDataConfigServiceImpl tripAdvisorDataConfigService;

    private List<TripAdvisorDto> tripAdvisorList;

    public void saveDataToJcr() {
        logger.info("Start: saveDataToJcr() method()");
        Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE, AppConstants.SERVICE_NAME_MVW_SERVICE_WRITER);

        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(params)) {
            Session session = resolver.adaptTo(Session.class);
            String targetCfStoragePath = StringUtils.trim(tripAdvisorDataConfigService.getCfStoredPath());
            int lastSlash = targetCfStoragePath.lastIndexOf("/");
            Pair<String, String> pairPathValues = Pair.of(
                targetCfStoragePath.substring(0, lastSlash),
                targetCfStoragePath.substring(lastSlash + 1)
            );
            String parentFolderPath = pairPathValues.getLeft();
            Resource contentRefsFolder = resolver.getResource(parentFolderPath);
            logger.info("TripAdvisor CF folder Path (config): {}, Parent Folder Path: {} ", targetCfStoragePath, parentFolderPath);

            if (contentRefsFolder == null) {
                logger.error("Exiting as we do not have folder for path: {}", parentFolderPath);
                return;
            }

            Resource tripAdvisorFolder = resolver.getResource(targetCfStoragePath);

            if (tripAdvisorFolder == null) {
                tripAdvisorFolder = resolver.create(contentRefsFolder, pairPathValues.getRight(), Collections.singletonMap(AppConstants.JCR_PRIMARY_TYPE, "sling:Folder"));
                createTripAdvisorCfFromData(resolver, tripAdvisorFolder);       // creating the CFs
            } else {
                updateTripAdvisorCfFromData(resolver, tripAdvisorFolder);       // updating the CFs
            }
            resolver.commit();      // Commiting the changes at the end

            activateAllCfs(session, tripAdvisorFolder);     // Replicating all the CFs inside the tripadvisor folder
        }  catch (PersistenceException e) {
            logger.error("Error in TripAdvisorScheduler: property={} & path={}", e.getPropertyName(), e.getResourcePath());
        } catch (Exception e) {
            logger.error("Error in TripAdvisorScheduler: ", e);
        }
    }

    private void activateAllCfs(Session session, Resource tripAdvisorFolder) {
        logger.info("Start: activateAllCfs() method()");
        for (Resource child : tripAdvisorFolder.getChildren()) {
            createAssetVersion(session, child.getPath(), "Auto version before publish");
            try {
                replicator.replicate(
                    session,
                    ReplicationActionType.ACTIVATE,
                    child.getPath()
                );
            } catch (Exception e) {
                logger.error("Replication failed for {}", child.getPath(), e);
            }
        }
    }

    public void createAssetVersion(Session session, String assetPath, String label) {
        logger.info("Start: createAssetVersion() method()");
        try {
            Node assetNode = session.getNode(assetPath);
            // Ensure the node is versionable
            if (!assetNode.isNodeType(AppConstants.NODE_TYPE_MIX_VERSIONABLE)) {
                assetNode.addMixin(AppConstants.NODE_TYPE_MIX_VERSIONABLE);
                session.save();     // persist mixin
            }

            VersionManager versionManager = session.getWorkspace().getVersionManager();
            Version version = versionManager.checkpoint(assetPath);     // creates a version

            logger.info("Created version: {} with label: {}", version.getName(), label);
        } catch (Exception e) {
            logger.error("Error while creating version of an asset: ", e);
        }
    }


    private void createTripAdvisorCfFromData(ResourceResolver resolver ,Resource tripAdvisorResource) throws PersistenceException {
        logger.info("Start: createTripAdvisorCfFromData() method");
        getTripAdvisorDataFromApi();        // This will fetch data and store it in tripAdvisorList object.

        if (CollectionUtils.isEmpty(tripAdvisorList)) {
            return;
        }

        for (TripAdvisorDto tripAdvisorObj : tripAdvisorList) {
            String propertyCode = tripAdvisorObj.getUniversalPropertyCode();
            if (StringUtils.isEmpty(propertyCode)) {
                continue;
            }
            createContentFragmentResource(resolver, tripAdvisorResource.getPath(), tripAdvisorObj);
        }
    }

    // We are taking the data from this API call
    private void getTripAdvisorDataFromApi() {
        tripAdvisorList = tripAdvisorListService.getResponseList().getList();
    }

    private void updateTripAdvisorCfFromData(ResourceResolver resolver, Resource tripAdvisorFolderRes) throws ContentFragmentException, PersistenceException {
        logger.info("Start: updateTripAdvisorCfFromData() method");
        getTripAdvisorDataFromApi(); // This will fetch data and store it in tripAdvisorList object.

        for (TripAdvisorDto tripDataObj : tripAdvisorList) {
            String tripAdvisorId = tripDataObj.getTripadvisorId();
            if (StringUtils.isEmpty(tripAdvisorId)) continue;

            logger.info("Updating the CF(s) for tripAdvisorID: {}", tripAdvisorId);
            String targetCfNodeName = createCfTitle(tripDataObj).toLowerCase();
            Resource cfRes = Optional.of(tripAdvisorId)
                .map(id -> tripAdvisorFolderRes.getPath().concat("/").concat(targetCfNodeName))
                .map(resolver::getResource).orElse(null);

            if (cfRes == null) {
                // <<<=====     If we do not have any CF, will create it for newly added resorts.   =====>>>
                createContentFragmentResource(resolver, tripAdvisorFolderRes.getPath(), tripDataObj);
            } else {
                // <<<=====     We will update the value for the old CFs for the available resorts.     =====>>>
                ContentFragment targetCf = Optional.ofNullable(cfRes).map(res -> res.adaptTo(ContentFragment.class)).orElse(null);
                if (!Objects.isNull(targetCf)) {
                    updateValuesInContentElements(tripDataObj, targetCf);
                }
            }
        }
    }

    private String createCfTitle(TripAdvisorDto tripAdvisorDto) {
        String tripAdvisorId = Optional.ofNullable(tripAdvisorDto.getTripadvisorId()).orElse(StringUtils.EMPTY);
        String universalPropertyCode = Optional.ofNullable(tripAdvisorDto.getUniversalPropertyCode()).orElse(StringUtils.EMPTY);
        return tripAdvisorId.concat(AppConstants.HYPHEN).concat(universalPropertyCode);
    }

    private void createContentFragmentResource(ResourceResolver resourceResolver, String destinationPath, TripAdvisorDto tripAdvisorDto) {
        logger.info("Start: createContentFragmentResource() method");
        Resource templateRes = resourceResolver.getResource(AppConstants.TRIPADVISOR_CF_MODEL_PATH);
        Resource cfParentRes = resourceResolver.getResource(destinationPath);
        if (templateRes != null && cfParentRes != null) {
            FragmentTemplate fragmentTemplate = templateRes.adaptTo(FragmentTemplate.class);
            Optional.ofNullable(fragmentTemplate).ifPresent(cfTemplate -> {
                try {
                    String cfTitle = createCfTitle(tripAdvisorDto);
                    String cfNodeName = cfTitle.toLowerCase();

                    logger.info("CF to be created for title: {} with nodeName: {}", cfTitle, cfNodeName);
                    ContentFragment contentFragment = fragmentTemplate.createFragment(cfParentRes, cfNodeName, cfTitle);
                    if (contentFragment != null) {
                        updateValuesInContentElements(tripAdvisorDto, contentFragment);
                        logger.info("Created CF Title={} & Name={}", contentFragment.getTitle(), contentFragment.getName());
                    }
                } catch (ContentFragmentException e) {
                    logger.error("Error while creating the Content Fragment: ", e);
                }
            });
        }
    }

    private void updateValuesInContentElements(TripAdvisorDto targetObj, ContentFragment contentFragment) throws ContentFragmentException {
        logger.info("Start: updateValuesInContentElements() method");
        Map<String, String> cfValues = new HashMap<>();
        cfValues.put(AppConstants.TRIPADVISOR_CF_MODEL_KEY_TRIP_ADVISOR_ID, StringUtils.defaultString(targetObj.getTripadvisorId()));
        cfValues.put(AppConstants.TRIPADVISOR_CF_MODEL_KEY_UNIVERSAL_PROPERTY_CODE, StringUtils.defaultString(targetObj.getUniversalPropertyCode()));
        cfValues.put(AppConstants.TRIPADVISOR_CF_MODEL_KEY_RATING, StringUtils.defaultString(targetObj.getRating()));
        cfValues.put(AppConstants.TRIPADVISOR_CF_MODEL_KEY_REVIEWS, StringUtils.defaultString(targetObj.getReviews()));
        cfValues.put(AppConstants.TRIPADVISOR_CF_MODEL_KEY_RATING_IMAGE, StringUtils.defaultString(targetObj.getRatingImage()));
        cfValues.put(AppConstants.TRIPADVISOR_CF_MODEL_KEY_WEB_URL, StringUtils.defaultString(targetObj.getWebUrl()));

        for (Map.Entry<String, String> entry : cfValues.entrySet()) {
            ContentElement element = contentFragment.getElement(entry.getKey());
            if (isValidToUpdateContentElement(element, entry)) {
                element.setContent(entry.getValue(), "text/plain");
            }
        }
    }

    private boolean isValidToUpdateContentElement(ContentElement element, Map.Entry<String, String> entry) throws ContentFragmentException {
        return element != null
            && entry.getValue() != null
            && !entry.getValue().isEmpty()
            && !entry.getValue().equals(element.getValue().getValue(String.class));
    }
}
