(ns clojure.type-id-namespace)

(defn type->id-namespace [property-type]
  (keyword (name property-type)))
