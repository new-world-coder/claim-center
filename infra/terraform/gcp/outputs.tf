output "gke_cluster_name" { value = google_container_cluster.claim_center.name }
output "artifact_registry" { value = google_artifact_registry_repository.images.id }
