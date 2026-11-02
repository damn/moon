(ns game.stage
  (:require [clojure.string :as str]
            [game.stage.inventory :as inventory-window]
            [lib.item :as item]
            [lib.mods :as mods]
            [lib.number :as number]
            [lib.stats :as stats]
            [lib.textures :as textures]
            [lib.timer :as timer]
            [ui.action-bar :as action-bar])
  (:import (com.badlogic.gdx Gdx)
           (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor Group Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Label Skin TextButton Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)
           (com.badlogic.gdx.utils Align)))

(def ^:private info-text-config
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

; TODO used for item/skill/entity
; logic should be in inventory window / action-bar and entity info window !
(defn info-text
  [entity elapsed-time]
  (let [{:keys [k->fn
                k-order-index
                k->colors]} info-text-config
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

(defn set-item! [skin stage textures cell item]
  (-> (.getRoot ^Stage stage)
      (.findActor "moon.ui.windows.inventory")
      (inventory-window/set-item! cell
                                  {:texture-region (textures/texture-region textures (:entity/image item))
                                   :tooltip-text (item/info-text item)}
                                  skin)))

(defn set-skill! [skin stage textures elapsed-time skill]
  (-> (.getRoot ^Stage stage)
      (.findActor "moon.ui.action-bar")
      (action-bar/add-skill! {:skill-id (:property/id skill)
                              :texture-region (textures/texture-region textures (:entity/image skill))
                              :tooltip-text (info-text skill elapsed-time)}
                             skin)))

(defn remove-item! [stage cell]
  (-> (.getRoot ^Stage stage)
      (.findActor "moon.ui.windows.inventory")
      (inventory-window/remove-item! cell)))

(defn sync-player-ui! [skin stage textures elapsed-time eid]
  (doseq [skill (vals (:entity/skills @eid))]
    (set-skill! skin stage textures elapsed-time skill))
  (doseq [[slot grid] (:entity/inventory @eid)
          x (range (count grid))
          y (range (count (grid 0)))
          :let [position [x y]
                item (get-in grid position)]
          :when item]
    (set-item! skin stage textures [slot position] item)))

(defn toggle-inventory-visible! [stage]
  (let [inventory (-> (.getRoot ^Stage stage)
                      (.findActor "moon.ui.windows.inventory"))]
    (.setVisible ^Actor inventory (not (.isVisible ^Actor inventory)))))

(defn close-windows! [stage]
  (->> (.getChildren ^Group (.findActor ^Group (.getRoot ^Stage stage) "moon.ui.windows"))
       (run! #(.setVisible ^Actor % false))))

(defn toggle-entity-info! [stage]
  (let [entity-info (.findActor ^Group (.getRoot ^Stage stage) "moon.ui.windows.entity-info")]
    (.setVisible ^Actor entity-info
                 (not (.isVisible ^Actor entity-info)))))

(defn mouseover-actor [stage x y]
  (.hit ^Stage stage (float x) (float y) true))

(defn ui-mouse-position [stage]
  (let [v2 (.unproject (.getViewport ^Stage stage)
                       (Vector2. (float (.getX Gdx/input))
                                 (float (.getY Gdx/input))))]
    [(.x v2) (.y v2)]))

(defn show-modal! [^Skin skin ^Stage stage {:keys [title text button-text on-click]}]
  (assert (not (.findActor (.getRoot stage) "moon.ui.modal-window")))
  (let [^String title title
        ^String text text
        window (Window. title skin)]
    (.add window (Label. text skin))
    (.row window)
    (.add window (doto (TextButton. ^String button-text skin)
                   (.addListener (proxy [ChangeListener] []
                                   (changed [_event _actor]
                                     (.remove (.findActor (.getRoot stage)
                                                          "moon.ui.modal-window"))
                                     (on-click))))))
    (.row window)
    (.pack window)
    (.setModal window true)
    (.setName window "moon.ui.modal-window")
    (.setPosition window
                  (/ (.getWorldWidth (.getViewport stage)) 2)
                  (float (* (.getWorldHeight (.getViewport stage)) (/ 3 4)))
                  (float Align/center))
    (.addActor stage window)))

(defn- create-info-window
  [{:keys [title
           actor-name
           visible?
           position
           set-label-text!
           skin]}]
  (let [^Skin skin skin
        label (Label. "MY LABEL TEXT" skin)
        window (Window. ^String title skin)
        c (.add window label)]
    (.expand c)
    (.row window)
    (.pack window)
    (.setName window actor-name)
    (.setVisible window visible?)
    (let [[x y] position]
      (.setPosition window (float x) (float y)))
    (.addActor window (proxy [Actor] []
                         (act [delta]
                           (let [^Actor this this]
                             (when (.getStage this)
                               (.setText label ^String (set-label-text!)))
                             (.pack window)
                             (proxy-super act delta)))
                         (draw [batch parent-alpha])))
    window))

(defn info-window-create
  [skin stage mouseover-eid world]
  (create-info-window
   {:title "Entity Info"
    :actor-name "moon.ui.windows.entity-info"
    :visible? false
    :position [(.getWorldWidth (.getViewport ^Stage stage)) 0]
    :set-label-text! (fn []
                       (if-let [eid @mouseover-eid]
                         (info-text (apply dissoc @eid [:entity/skills
                                                        :entity/faction
                                                        :active-skill])
                                    @(:world/elapsed-time @world))
                         ""))
    :skin skin}))
