(ns moon.schemas)

(defprotocol Schemas
  (create-map-schema [schemas ks])
  (validate [_ k value])
  (build-values [_ property db])
  (map-keys [schemas schema])
  (optional? [schemas schema k])
  (optional-keyset [schemas schema])
  (default-value [schemas k]))
