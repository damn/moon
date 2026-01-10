(ns moon.schemas)

(defprotocol Schemas
  (build-values [_ property db])
  (default-value [_ k])
  (validate [_ k value])
  (create-map-schema [_ ks]))
