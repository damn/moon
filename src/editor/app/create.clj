(ns editor.app.create)

(defn f!
  [ctx create-map-widget-table]
  (assoc ctx
         :ctx/create-map-widget-table create-map-widget-table
         :ctx/property-k-sort-order [:property/id
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
                                     :skill/cooldown]))
