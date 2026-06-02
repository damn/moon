(ns moon.property.type)

(defn property-type [{:keys [property/id]}]
  (keyword "properties" (namespace id)))
