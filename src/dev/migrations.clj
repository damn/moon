(ns dev.migrations
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def file "properties.edn")

(defn read-properties []
  (-> file io/resource slurp edn/read-string))

(def stat-keys
  [:stats/modifiers
   :stats/hp
   :stats/movement-speed
   :stats/aggro-range
   :stats/reaction-time
   :stats/mana
   :stats/strength
   :stats/cast-speed
   :stats/attack-speed
   :stats/armor-save
   :stats/armor-pierce])

(defn move-stats-in-separate-component [creature]
  (-> (apply dissoc creature stat-keys)
      (assoc :entity/stats (select-keys creature stat-keys))))

(def creature? :creature/level)

#_(defn update-properties-file! []
  (let [new-data (for [property (read-properties)]
                   (if (creature? property)
                     (move-stats-in-separate-component property)
                     property))]
    (db/save-vals! new-data
                   (io/resource file))))
(comment

 (count (filter creature? (read-properties)))
 (clojure.pprint/pprint (first (filter creature? (read-properties))))

 (db/do! {} {:schemas "schema.edn"
             :properties "properties.edn"})

 (def example-creature {:stats/aggro-range 6,
                        :entity/animation
                        {:frame-duration 0.1,
                         :frames
                         [{:file "images/animations/toad-horned-1.png"}
                          {:file "images/animations/toad-horned-2.png"}
                          {:file "images/animations/toad-horned-3.png"}
                          {:file "images/animations/toad-horned-4.png"}],
                         :looping? true},
                        :creature/level 1,
                        :stats/reaction-time 12,
                        :stats/mana 11,
                        :property/pretty-name "Toad-horned",
                        :stats/strength 1,
                        :entity/species :species/toad,
                        :entity/body #:body{:flying? false, :height 11/24, :width 2/3},
                        :stats/movement-speed 1.6,
                        :stats/hp 12,
                        :property/id :creatures/toad-horned,
                        :entity/skills #{:skills/melee-attack}})

 (clojure.pprint/pprint
  (move-stats-in-separate-component example-creature))
 {:entity/animation
  {:frame-duration 0.1,
   :frames
   [{:file "images/animations/toad-horned-1.png"}
    {:file "images/animations/toad-horned-2.png"}
    {:file "images/animations/toad-horned-3.png"}
    {:file "images/animations/toad-horned-4.png"}],
   :looping? true},
  :creature/level 1,
  :property/pretty-name "Toad-horned",
  :entity/species :species/toad,
  :entity/body #:body{:flying? false, :height 11/24, :width 2/3},
  :entity/stats
  #:entity{:hp 12,
           :movement-speed 1.6,
           :aggro-range 6,
           :reaction-time 12,
           :mana 11,
           :strength 1},
  :property/id :creatures/toad-horned,
  :entity/skills #{:skills/melee-attack}}
 )
