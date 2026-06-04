(ns editor.widget.map
  (:require [clojure.core.sort-by-k-order :refer [sort-by-k-order]]
            [clojure.set :as set]
            [editor.map-widget-table.get-value :as get-value]
            [editor.widget :as widget]
            [moon.schemas.optional-keyset :refer [optional-keyset]]
            [moon.schemas.optional :refer [optional?]]))

(defmethod widget/create :s/map
  [schema
   m
   {:keys [ctx/db
           ctx/skin
           ctx/create-map-widget-table
           ctx/property-k-sort-order
           ctx/create-component-row
           ]
    :as ctx}]
  (let [schemas (:db/schemas db)]
    (create-map-widget-table
     {:create-component-row create-component-row
      :skin skin
      :schema schema
      :k->widget (into {}
                       (for [[k v] m]
                         [k (widget/build ctx (get schemas k) k v)]))
      :k->optional? #(optional? schemas schema %)
      :ks-sorted (map first (sort-by-k-order property-k-sort-order m))
      :opt? (seq (set/difference (optional-keyset schemas schema)
                                 (set (keys m))))})))

(defmethod widget/value :s/map
  [_ table schemas]
  (get-value/f table schemas))
