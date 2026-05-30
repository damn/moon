(ns editor-widget.map
  (:require [clojure.core-ext :refer [sort-by-k-order]]
            [clojure.set :as set]
            [game.ctx :as ctx]
            [game.schema :as schema]
            [moon.schemas :as schemas]
            [moon.ui.map-widget-table :as map-widget-table]))

(def ^:private property-k-sort-order
  [:property/id
   :property/pretty-name
   :entity/image
   :entity/animation
   :entity/species
   :creature/level
   :entity/body
   :item/slot
   :projectile/speed
   :projectile/max-range
   :projectile/piercing?
   :skill/action-time-modifier-key
   :skill/action-time
   :skill/start-action-sound
   :skill/cost
   :skill/cooldown])

(defmethod schema/create :s/map
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

(defmethod schema/value :s/map
  [_ table schemas]
  (map-widget-table/get-value table schemas))
