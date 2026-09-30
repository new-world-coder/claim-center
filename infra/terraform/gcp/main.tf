terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.0"
    }
  }
}

provider "google" {
  project = var.project_id
  region  = var.region
}

resource "google_container_cluster" "claim_center" {
  name                     = "claim-center-gke"
  location                 = var.region
  remove_default_node_pool = true
  initial_node_count       = 1
}

resource "google_container_node_pool" "default_pool" {
  name       = "default-pool"
  location   = var.region
  cluster    = google_container_cluster.claim_center.name
  node_count = 3
  node_config {
    machine_type = "e2-standard-4"
    oauth_scopes = ["https://www.googleapis.com/auth/cloud-platform"]
  }
}

resource "google_sql_database_instance" "postgres" {
  name             = "claim-center-postgres"
  database_version = "POSTGRES_16"
  region           = var.region
  settings {
    tier = "db-custom-2-7680"
  }
  deletion_protection = false
}

resource "google_sql_database" "claim_center" {
  name     = "claim_center"
  instance = google_sql_database_instance.postgres.name
}

resource "google_artifact_registry_repository" "images" {
  location      = var.region
  repository_id = "claim-center-images"
  format        = "DOCKER"
}
