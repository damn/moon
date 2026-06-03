(ns editor.widget.map
  (:require [clojure.core.sort-by-k-order :refer [sort-by-k-order]]
            [clojure.set :as set]
            [editor.map-widget-table :as map-widget-table]
            [editor.map-widget-table.get-value :as get-value]
            [editor.widget :as widget]
            [moon.schemas :as schemas]))

(defmethod widget/create :s/map
  [schema
   m
   {:keys [ctx/db
           ctx/skin
           ctx/property-k-sort-order]
    :as ctx}]
  (let [schemas (:db/schemas db)]
    (map-widget-table/create
     {:skin skin
      :schema schema
      :k->widget (into {}
                       (for [[k v] m]
                         [k (widget/build ctx (get schemas k) k v)]))
      :k->optional? #(schemas/optional? schemas schema %)
      :ks-sorted (map first (sort-by-k-order property-k-sort-order m))
      :opt? (seq (set/difference (schemas/optional-keyset schemas schema)
                                 (set (keys m))))})))

(defmethod widget/value :s/map
  [_ table schemas]
  (get-value/f table schemas))
