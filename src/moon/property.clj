(ns moon.property
  "A property in the database.

  The type is implicit in the `:property/id`.

  For example:

  `:property/id :creatures/toad-horned`

  => type is `:properties/creatures`."
  (:refer-clojure :exclude [type])
  (:require [clojure.pprint :refer [pprint]]))

(defn type [{:keys [property/id]}]
  (keyword "properties" (namespace id)))

(defn type->id-namespace [property-type]
  (keyword (name property-type)))

(defn image [{:keys [entity/image entity/animation]}]
  (or image
      (first (:animation/frames animation))))

(defn tooltip [property]
  (binding [*print-level* 2]
    (with-out-str
     (pprint property))))
