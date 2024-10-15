(ns core.property
  (:refer-clojure :exclude [def type])
  (:require [core.component :refer [defc] :as component]
            [core.data :as data]
            [malli.core :as m]
            [malli.error :as me]))

(defc :property/id {:data :qualified-keyword})

(defn def [k {:keys [schema overview]}]
  (defc k
    {:data [:map (conj schema :property/id)]
     :overview overview}))

(defn type->id-namespace [property-type]
  (keyword (name property-type)))

(defn type [{:keys [property/id]}]
  (keyword "properties" (namespace id)))

(defn ->schema [property]
  (-> property
      type
      data/component
      data/schema
      m/schema))

(defn- invalid-ex-info [schema value]
  (ex-info (str (me/humanize (m/explain schema value)))
           {:value value
            :schema (m/form schema)}))

(defn validate [property]
  (let [schema (->schema property)]
    (when-not (m/validate schema property)
      (throw (invalid-ex-info schema property)))))

(defn ->image [{:keys [entity/image entity/animation]}]
  (or image
      (first (:frames animation))))

(defn types []
  (filter #(= "properties" (namespace %)) (keys component/attributes)))

(defn overview [property-type]
  (:overview (get component/attributes property-type)))
