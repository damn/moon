(ns clojure.editor.create-widget-s-map
  (:require [clojure.editor.create-widget :refer [create-widget]]
            [clojure.editor.create-widget-build-widget :as build-widget]
            [clojure.editor.create-widget-map-widget-table-create :as map-widget-table-create]
            [clojure.editor.property-k-sort-order :refer [property-k-sort-order]]
            [clojure.optional :refer [optional?]]
            [clojure.schemas-optional-keyset :refer [optional-keyset]]
            [clojure.set :as set]
            [clojure.sort-by-k-order :refer [sort-by-k-order]]))

(defmethod create-widget :s/map
  [schema
   m
   {:keys [ctx/db
           ctx/skin]
    :as ctx}]
  (let [schemas (:db/schemas db)]
    (map-widget-table-create/map-widget-table-create
     {:skin skin
      :schema schema
      :k->widget (into {}
                       (for [[k v] m]
                         [k (build-widget/build-widget ctx (get schemas k) k v)]))
      :k->optional? #(optional? schemas schema %)
      :ks-sorted (map first (sort-by-k-order property-k-sort-order m))
      :opt? (seq (set/difference (optional-keyset schemas schema)
                                 (set (keys m))))})))
