(ns moon.property
  (:refer-clojure :exclude [type])
  (:require [clojure.pprint :refer [pprint]]))

(defn type [{:keys [property/id]}]
  (keyword "properties" (namespace id)))

(defn type->id-namespace [property-type]
  (keyword (name property-type)))

(defn image [{:keys [entity/image entity/animation]}]
  (or image
      (first (:animation/frames animation))))

(defn tooltip ^String [property]
  (binding [*print-level* 2]
    (with-out-str
      (pprint property))))
