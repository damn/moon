(ns clojure.sort-by-k-order)

(comment

 ; simpler way to do 'sort-by-k-order':
 ; But disadvantage: need to (zipmap order (range)) first ....
 ; so 'create-order' first ...
 ; but good idea to reduce it to a 'sort-by' function first param...

 (index-of/f [:a :b :foo :c] :foo)
 (contains? [:a :b :foo :c] :foo)
 (def order [:low :medium :high])
 (def items [:high :low :medium :low :high])

 ;; build order lookup map
 (def order-map (zipmap order (range)))

 (sort-by order-map items)
 ;; => (:low :low :medium :high :high)
 )

(def property-k-sort-order
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
   ]
  )

(comment
 (sort-by (zipmap property-k-sort-order (range))
          {:entity/image "IMAGE"
           :skill/cost 3
           })
 )
