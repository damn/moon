(ns editor.widget.map
  (:require [clojure.core-ext :refer [sort-by-k-order]]
            [clojure.set :as set]
            [editor.constants :refer [property-k-sort-order]]
            [editor.map-widget-table :as map-widget-table]
            [editor.widget :as widget]
            [moon.schemas :as schemas]))

(defmethod widget/create :s/map
  [schema
   m
   {:keys [ctx/db
           ctx/skin]
    :as ctx}]
  (let [schemas (:db/schemas db)]
    (map-widget-table/create
     {:skin skin
      :schema schema
      :k->widget (into {}
                       (for [[k v] m]
                         [k (map-widget-table/build-value-widget ctx (get schemas k) k v)]))
      :k->optional? #(schemas/optional? schemas schema %)
      :ks-sorted (map first (sort-by-k-order property-k-sort-order m))
      :opt? (seq (set/difference (schemas/optional-keyset schemas schema)
                                 (set (keys m))))})))

(defmethod widget/value :s/map
  [_ table schemas]
  (map-widget-table/get-value table schemas))
