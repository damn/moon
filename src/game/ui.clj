(ns game.ui
  (:require [clojure.string :as str]
            [moon.item :as item]
            [moon.mods :as mods]
            [moon.number :as number]
            [moon.stats :as stats]
            [moon.textures :as textures]
            [moon.timer :as timer]
            [ui.action-bar :as action-bar])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor Group Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Image Skin TextTooltip)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable TextureRegionDrawable)))

(def ^:private info
  {:k->fn {:creature/level (fn [v _elapsed-time]
                              (str "Level: " v))
           :entity/stats (fn [entity-stats _elapsed-time]
                           (stats/format-text entity-stats))
           :effects.target/convert (fn [_ _elapsed-time]
                                    "Converts target to your side.")
           :effects.target/damage (fn [{[min max] :damage/min-max} _elapsed-time]
                                    (str min "-" max " damage"))
           :effects.target/kill (fn [_ _elapsed-time]
                                  "Kills target")
           :effects.target/melee-damage (fn [_ _elapsed-time]
                                          "Damage based on entity strength.")
           :effects.target/spiderweb (fn [_ _elapsed-time]
                                       "Spiderweb slows 50% for 5 seconds.")
           :effects.target/stun (fn [duration _elapsed-time]
                                  (str "Stuns for " (number/readable duration) " seconds"))
           :effects/spawn (fn [{:keys [property/pretty-name]} _elapsed-time]
                            (str "Spawns a " pretty-name))
           :effects/target-all (fn [_ _elapsed-time]
                                 "All visible targets")
           :entity/delete-after-duration (fn [counter elapsed-time]
                                             (str "Remaining: " (number/readable (timer/ratio elapsed-time counter)) "/1"))
           :entity/faction (fn [faction _elapsed-time]
                             (str "Faction: " (name faction)))
           :entity/fsm (fn [fsm _elapsed-time]
                          (str "State: " (name (:state fsm))))
           :stats/modifiers (fn [mods _elapsed-time]
                              (mods/format-text mods))
           :entity/skills (fn [skills _elapsed-time]
                            (when (seq skills)
                              (str "Skills: " (str/join "," (map name (keys skills))))))
           :entity/species (fn [species _elapsed-time]
                             (str "Creature - " (str/capitalize (name species))))
           :entity/temp-modifier (fn [{:keys [counter]} elapsed-time]
                                    (str "Spiderweb - remaining: " (number/readable (timer/ratio elapsed-time counter)) "/1"))
           :projectile/piercing? (fn [_ _elapsed-time]
                                   "Piercing")
           :property/pretty-name (fn [v _elapsed-time]
                                    v)
           :skill/cooling-down? (fn [counter elapsed-time]
                                   (str "Cooldown: " (number/readable (timer/ratio elapsed-time counter)) "/1"))
           :skill/action-time (fn [v _elapsed-time]
                                 (str "Action-Time: " (number/readable v) " seconds"))
           :skill/action-time-modifier-key (fn [v _elapsed-time]
                                              (case v
                                                :stats/cast-speed "Spell"
                                                :stats/attack-speed "Attack"))
           :skill/cooldown (fn [v _elapsed-time]
                             (str "Cooldown: " (number/readable v) " seconds"))
           :skill/cost (fn [v _elapsed-time]
                          (str "Cost: " v " Mana"))
           :maxrange (fn [v _elapsed-time]
                       (str "Range: " v " Meters."))}
   :k-order-index (zipmap [:property/pretty-name
                           :skill/action-time-modifier-key
                           :skill/action-time
                           :skill/cooldown
                           :skill/cost
                           :skill/effects
                           :entity/species
                           :creature/level
                           :entity/stats
                           :entity/delete-after-duration
                           :projectile/piercing?
                           :entity/projectile-collision
                           :maxrange
                           :entity-effects]
                          (range))
   :k->colors {:property/pretty-name "PRETTY_NAME"
               :stats/modifiers "CYAN"
               :maxrange "LIGHT_GRAY"
               :creature/level "GRAY"
               :projectile/piercing? "LIME"
               :skill/action-time-modifier-key "VIOLET"
               :skill/action-time "GOLD"
               :skill/cooldown "SKY"
               :skill/cost "CYAN"
               :entity/delete-after-duration "LIGHT_GRAY"
               :entity/faction "SLATE"
               :entity/fsm "YELLOW"
               :entity/species "LIGHT_GRAY"
               :entity/temp-modifier "LIGHT_GRAY"}})

(defn- remove-newlines [s]
  (let [new-s (-> s
                  (str/replace "\n\n" "\n")
                  (str/replace #"^\n" "")
                  str/trim-newline)]
    (if (= (count new-s) (count s))
      s
      (remove-newlines new-s))))

(defn info-text
  [entity elapsed-time]
  (let [{:keys [k->fn
                k-order-index
                k->colors]} info
        component-info (fn [[k v]]
                         (let [s (if-let [info-fn (k->fn k)]
                                   (str (info-fn v elapsed-time)))]
                           (if-let [color (k->colors k)]
                             (str "[" color "]" s "[]")
                             s)))]
    (->> entity
         (sort-by (fn [[k _]] (get k-order-index k Integer/MAX_VALUE)))
         (keep (fn [{k 0 v 1 :as component}]
                 (str (try (component-info component)
                           (catch Throwable _t
                             (str "*info-error* " k)))
                      (when (map? v)
                        (str "\n" (info-text v elapsed-time))))))
         (str/join "\n")
         remove-newlines)))

(defn- inventory-window-get-cell [inventory-window cell]
  (->> (.getChildren ^Group (.findActor ^Group inventory-window "inventory-cell-table"))
       (filter #(= (.getUserObject ^Actor %) cell))
       first))

(defn- inventory-window-remove-item! [inventory-window cell]
  (let [cell-widget (inventory-window-get-cell inventory-window cell)
        image-widget (.findActor ^Group cell-widget "image-widget")]
    (.setDrawable ^Image image-widget ^Drawable (:background-drawable (.getUserObject ^Actor image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))

(defn- inventory-window-set-item! [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (inventory-window-get-cell inventory-window cell)
        image-widget (.findActor ^Group cell-widget "image-widget")
        cell-size (:cell-size (.getUserObject ^Actor image-widget))]
    (.setDrawable ^Image image-widget ^Drawable (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                                                  (.setMinSize cell-size cell-size)))
    (.addListener ^Actor cell-widget (TextTooltip. ^String tooltip-text ^Skin skin))
    nil))

(defn ui-set-item! [skin stage textures cell item]
  (-> (.getRoot ^Stage stage)
      (.findActor "moon.ui.windows.inventory")
      (inventory-window-set-item! cell
                                  {:texture-region (textures/texture-region textures (:entity/image item))
                                   :tooltip-text (item/info-text item)}
                                  skin)))

(defn ui-set-skill! [skin stage textures elapsed-time skill]
  (-> (.getRoot ^Stage stage)
      (.findActor "moon.ui.action-bar")
      (action-bar/add-skill! {:skill-id (:property/id skill)
                              :texture-region (textures/texture-region textures (:entity/image skill))
                              :tooltip-text (info-text skill elapsed-time)}
                             skin)))

(defn ui-remove-item! [stage cell]
  (-> (.getRoot ^Stage stage)
      (.findActor "moon.ui.windows.inventory")
      (inventory-window-remove-item! cell)))

(defn sync-player-ui! [skin stage textures elapsed-time eid]
  (doseq [skill (vals (:entity/skills @eid))]
    (ui-set-skill! skin stage textures elapsed-time skill))
  (doseq [[slot grid] (:entity/inventory @eid)
          [position item] grid
          :when item]
    (ui-set-item! skin stage textures [slot position] item)))

(defn toggle-inventory-visible! [stage]
  (let [inventory (-> (.getRoot ^Stage stage)
                      (.findActor "moon.ui.windows.inventory"))]
    (.setVisible ^Actor inventory (not (.isVisible ^Actor inventory)))))
