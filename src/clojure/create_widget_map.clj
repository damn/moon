(ns clojure.create-widget-map
  (:require [clojure.sort-by-k-order :refer [sort-by-k-order]]
            [clojure.set :as set]
            [clojure.build-widget :as build-widget]
            [clojure.schemas-optional-keyset :refer [optional-keyset]]
            [clojure.optional :refer [optional?]]))

(defn f
  [schema
   m
   {:keys [ctx/db
           ctx/skin
           ctx/create-map-widget-table
           ctx/property-k-sort-order
           ctx/create-component-row]
    :as ctx}]
  (let [schemas (:db/schemas db)]
    (create-map-widget-table
     {:create-component-row create-component-row
      :skin skin
      :schema schema
      :k->widget (into {}
                       (for [[k v] m]
                         [k (build-widget/f ctx (get schemas k) k v)]))
      :k->optional? #(optional? schemas schema %)
      :ks-sorted (map first (sort-by-k-order property-k-sort-order m))
      :opt? (seq (set/difference (optional-keyset schemas schema)
                                 (set (keys m))))})))
