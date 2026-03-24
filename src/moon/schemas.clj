(ns moon.schemas)

(defprotocol Schemas
  (build-values [schemas property db])
  (default-value [schemas k])
  (validate [schemas k value])
  (create-map-schema [schemas ks]))
