(ns moon.game
  (:require [clojure.edn :as edn]

            ; clojure concept
            [moon.rand :refer [int-between]]

            [moon.inventory-window :as inventory-window :refer [inventory-window-build]]
            [moon.inventory :as inventory]
            [moon.inventory.cell :as inventory-cell]
            [moon.input :as input]

            ; clojure conept
            [moon.number :as number]

            [moon.item :as item]

            [clojure.java.io :as io]

            ; TODO game should not depend on specific level ?
            [moon.level.uf-caves :as uf-caves]
            [moon.level.modules :as modules]
            [moon.level.tmx :as tmx]

            ; FIXME - what concept ?
            [moon.schema.register-methods]
            [moon.malli :as malli-schema]

            [clojure.math :as math]
            [moon.faction :as faction]

            [moon.movement-property :as movement-property]
            [moon.orthographic-camera :as orthographic-camera]
            [moon.grid.point-to-entities :refer [point->entities]]
            [clojure.projectile-start-point :as projectile-start-point]
            [clojure.ratio :as timer-ratio]
            [clojure.readable :as readable]
            [clojure.remove-from-occupied-cells :refer [remove-from-occupied-cells!]]
            [clojure.remove-from-touched-cells :refer [remove-from-touched-cells!]]
            [clojure.remove-newlines :refer [remove-newlines]]
            [moon.m :refer [safe-merge]]
            [moon.stage :as moon-stage]
            [moon.button :refer [is?]]
            [moon.window :refer [title-bar?]]
            [clojure.set-ctx :as set-ctx]
            [clojure.k-order :as k-order]
            [clojure.sort-by-order :as sort-by-order]
            [moon.tiled-map.spawn-positions :as spawn-positions]
            [clojure.stopped :refer [stopped?]]
            [clojure.string :as str]
            [clojure.table-set-opts :as table-set-opts]
            [clojure.throwable :as throwable]
            [moon.tiled-map :as moon-tiled-map]
            [clojure.timer-create :refer [create-timer]]
            [moon.txs-fn-map :refer [actions!]]
            [clojure.try-move-solid-body :as try-move-solid-body]
            [moon.info-window :as info-window]
            [moon.action-bar :as action-bar]
            [moon.data-viewer-window :as data-viewer-window]
            [moon.dev-menu :as dev-menu]
            [moon.error-window :as error-window]
            [moon.viewport :refer [unproject]]
            [moon.val-max :refer [ratio]]
            [com.badlogic.gdx.graphics :as graphics]
            [com.badlogic.gdx.graphics.gl20 :as gl20]
            [com.badlogic.gdx.graphics.color :as color]
            [com.badlogic.gdx.graphics.colors :as colors]
            [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]
            [com.badlogic.gdx.graphics.g2d.bitmap-font$bitmap-font-data :as bitmap-font-data]
            [com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.graphics.gl20 :as gl20]
            [com.badlogic.gdx.graphics.glutils.pixmap-texture-data :as pixmap-texture-data]
            [moon.pixmap :as pixmap]
            [com.badlogic.gdx.graphics.texture :as texture]
            [com.badlogic.gdx.graphics.texture$texture-filter :as texture-filter]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [com.badlogic.gdx.scenes.scene2d.ui.button-group :as button-group]
            [com.badlogic.gdx.scenes.scene2d.ui.horizontal-group :as horizontal-group]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as gdx-window]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]
            [com.badlogic.gdx.utils.align :as align]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]
            [moon.application :as application]
            [moon.font-generator :as font-generator]
            [moon.map-properties :as map-properties]
            [moon.files :as files]
            [gdx.graphics.g2d.batch.draw-tiled-map :as draw-tiled-map]
            [moon.disposable :as disposable]
            [moon.audio :as audio]
            [moon.body :as body]
            [moon.cell :as cell]
            [moon.content-grid :as content-grid]
            [moon.db :as db]
            [moon.g2d :as moon-g2d]
            [moon.grid :as grid :refer [set-occupied-cells! set-touched-cells!]]
            [moon.item :as item]
            [moon.mods :as mods]
            [moon.raycaster :as raycaster]
            [moon.stats :as stats]
            [moon.textures :as textures]
            [moon.timer :as timer]
            [moon.v2 :as v2]
            [qrecord.core :as q]
            [reduce-fsm :as fsm]
            [space.earlygrey.shapedrawer.shape-drawer :as shape-drawer])
  (:gen-class))

(q/defrecord R [])

(q/defrecord EntityRecord [entity/body])

(q/defrecord BodyRecord [body/position
                         body/width
                         body/height
                         body/collides?
                         body/z-order
                         body/rotation-angle])

(def minimum-size 0.39)

(def max-delta 0.04)

(def level-fn uf-caves/create)

(def pausing? true)

(def state->pause-game?
  {:active-skill false
   :stunned false
   :player-moving false
   :player-idle true
   :player-dead true
   :player-item-on-cursor true})

(def factions-iterations
  {:good 15
   :evil 5})

(def spiderweb-modifiers {:modifier/movement-speed {:op/mult -50}})
(def spiderweb-duration 5)

(def world-unit-scale (float (/ 48)))

(def z-orders
  [:z-order/on-ground
   :z-order/ground
   :z-order/flying
   :z-order/effect])

(defn affected-targets
  [active-entities raycaster entity]
  (->> active-entities
       (filter #(:entity/species @%))
       (filter #(raycaster/line-of-sight? raycaster entity @%))
       (remove #(:entity/player? @%))))

(defmulti handle-effect
  (fn [[k _v] _effect-ctx _ctx]
    k))

(defmethod handle-effect :effects/audiovisual
  [[_ audiovisual] {:keys [effect/target-position]} _ctx]
  [[:tx/audiovisual target-position audiovisual]])

(defmethod handle-effect :effects/projectile
  [[_ projectile] {:keys [effect/source effect/target-direction]} _ctx]
  [[:tx/spawn-projectile
    {:position (projectile-start-point/f (:entity/body @source)
                                         target-direction
                                         (:projectile/size projectile))
     :direction target-direction
     :faction (:entity/faction @source)}
    projectile]])

(defmethod handle-effect :effects/spawn
  [[_ {:keys [property/id] :as property}]
   {:keys [effect/source effect/target-position]}
   _ctx]
  [[:tx/spawn-creature {:position target-position
                        :creature-property property
                        :components {:entity/fsm {:fsm :fsms/npc
                                                  :initial-state :npc-idle}
                                     :entity/faction (:entity/faction @source)}}]])

(defmethod handle-effect :effects/target-all
  [[_ {:keys [entity-effects]}]
   {:keys [effect/source]}
   {:keys [ctx/active-entities
           ctx/colors
           ctx/raycaster]}]
  (let [source* @source]
    (apply concat
           (for [target (affected-targets active-entities raycaster source*)]
             [[:tx/spawn-line
               {:start (:body/position (:entity/body source*)) #_(start-point source* target*)
                :end (:body/position (:entity/body @target))
                :duration 0.05
                :color (:colors/target-all-line colors)
                :thick? true}]
              [:tx/effect
               {:effect/source source
                :effect/target target}
               entity-effects]]))))

(defmethod handle-effect :effects/target-entity
  [[_ {:keys [maxrange entity-effects]}]
   {:keys [effect/source effect/target] :as effect-ctx}
   {:keys [ctx/colors]}]
  (let [body        (:entity/body @source)
        target-body (:entity/body @target)]
    (if (body/in-range? body target-body maxrange)
      [[:tx/spawn-line {:start (body/start-point body target-body)
                        :end (:body/position target-body)
                        :duration 0.05
                        :color (:colors/target-entity-line colors)
                        :thick? true}]
       [:tx/effect effect-ctx entity-effects]]
      [[:tx/audiovisual
        (body/end-point body target-body maxrange)
        :audiovisuals/hit-ground]])))

(defmethod handle-effect :effects.target/audiovisual
  [[_ audiovisual] {:keys [effect/target]} _ctx]
  [[:tx/audiovisual (:body/position (:entity/body @target)) audiovisual]])

(defmethod handle-effect :effects.target/convert
  [_ {:keys [effect/source effect/target]} _ctx]
  [[:tx/assoc target :entity/faction (:entity/faction @source)]])

(defmethod handle-effect :effects.target/damage
  [[_ damage] {:keys [effect/source effect/target]} _ctx]
  (let [source* @source
        target* @target
        hp (stats/get-hitpoints (:entity/stats target*))]
    (cond
     (zero? (hp 0))
     nil

     ; TODO find a better way
     (not (:entity/stats target*))
     nil

     (and (:entity/stats source*)
          (:entity/stats target*)
          (< (rand) (stats/effective-armor-save (:entity/stats source*)
                                            (:entity/stats target*))))
     [[:tx/add-text-effect target "[WHITE]ARMOR" 0.3]]

     :else
     (let [min-max (if (:entity/stats source*)  ; projectiles dont have ....
                     (:damage/min-max (stats/calc-damage (:entity/stats source*)
                                                     (:entity/stats target*)
                                                     damage))
                     (:damage/min-max damage))
           dmg-amount (int-between min-max)
           new-hp-val (max (- (hp 0) dmg-amount)
                           0)]
       [[:tx/assoc-in target [:entity/stats :stats/hp 0] new-hp-val]
        [:tx/event    target (if (zero? new-hp-val) :kill :alert)]
        [:tx/audiovisual (:body/position (:entity/body target*)) :audiovisuals/damage]
        [:tx/add-text-effect target (str "[RED]" dmg-amount "[]") 0.3]]))))

(defmethod handle-effect :effects.target/kill
  [_ {:keys [effect/target]} _ctx]
  [[:tx/event target :kill]])

(defmethod handle-effect :effects.target/melee-damage
  [_ {:keys [effect/source] :as effect-ctx} ctx]
  ; TODO AT EFFECT CREATION MAKE
  ; same @ applicable
  (handle-effect [:effects.target/damage (stats/melee-damage @source)] effect-ctx ctx))

(defmethod handle-effect :effects.target/spiderweb
  [_ {:keys [effect/target]} {:keys [ctx/elapsed-time]}]
  ; TODO stacking? (if already has k ?) or reset counter ? (see string-effect too)
  (when-not (:entity/temp-modifier @target)
    [[:tx/assoc target :entity/temp-modifier {:modifiers spiderweb-modifiers
                                              :counter (create-timer elapsed-time spiderweb-duration)}]
     [:tx/update target :entity/stats stats/add-mods spiderweb-modifiers]]))

(defmethod handle-effect :effects.target/stun
  [[_ duration] {:keys [effect/target]} _ctx]
  [[:tx/event target :stun duration]])

(defmulti effect-applicable?
  (fn [[k _v] _effect-ctx]
    k))

(defmethod effect-applicable? :effects/audiovisual
  [_ {:keys [effect/target-position]}]
  target-position)

(defmethod effect-applicable? :effects/projectile
  [_ {:keys [effect/target-direction]}]
  target-direction)

(defmethod effect-applicable? :effects/spawn
  [_ {:keys [effect/source effect/target-position]}]
  (and (:entity/faction @source)
       target-position))

(defmethod effect-applicable? :effects/target-all
  [_ _]
  true)

(defmethod effect-applicable? :effects/target-entity
  [[_ {:keys [entity-effects]}] {:keys [effect/target] :as effect-ctx}]
  (and target
       (seq (filter #(effect-applicable? % effect-ctx) entity-effects))))

(defmethod effect-applicable? :effects.target/audiovisual
  [_ {:keys [effect/target]}]
  target)

(defmethod effect-applicable? :effects.target/convert
  [_ {:keys [effect/source effect/target]}]
  (and target
       (= (:entity/faction @target)
          (faction/enemy (:entity/faction @source)))))

(defmethod effect-applicable? :effects.target/damage
  [_ {:keys [effect/target]}]
  (and target
       #_(:stats/hp @target)))

(defmethod effect-applicable? :effects.target/kill
  [_ {:keys [effect/target]}]
  (and target
       (:entity/fsm @target)))

(defmethod effect-applicable? :effects.target/melee-damage
  [_ {:keys [effect/source] :as effect-ctx}]
  (effect-applicable? [:effects.target/damage (stats/melee-damage @source)] effect-ctx))

(defmethod effect-applicable? :effects.target/spiderweb
  [_ {:keys [effect/target]}]
  (:entity/stats @target))

(defmethod effect-applicable? :effects.target/stun
  [_ {:keys [effect/target]}]
  (and target
       (:entity/fsm @target)))

(defmulti effect-useful?
  (fn [[k _v] _effect-ctx _ctx]
    k))

(defmethod effect-useful? :default
  [_ _effect-ctx _ctx]
  true)

(defmethod effect-useful? :effects/audiovisual
  [_ _effect-ctx _ctx]
  false)

(defmethod effect-useful? :effects/projectile
  [[_ {:keys [projectile/max-range] :as projectile}]
   {:keys [effect/source effect/target]}
   {:keys [ctx/raycaster]}]
  (let [source-p (:body/position (:entity/body @source))
        target-p (:body/position (:entity/body @target))]
    (and (not (let [[start1,target1,start2,target2] (v2/double-ray-endpositions source-p
                                                                               target-p
                                                                               (:projectile/size projectile))]
                (or
                 (raycaster/blocked? raycaster start1 target1)
                 (raycaster/blocked? raycaster start2 target2))))
         (< (v2/distance source-p target-p)
            max-range))))

(defmethod effect-useful? :effects/target-all
  [_ _effect-ctx _ctx]
  false)

(defmethod effect-useful? :effects/target-entity
  [[_ {:keys [maxrange]}] {:keys [effect/source effect/target]} _ctx]
  (body/in-range? (:entity/body @source)
                  (:entity/body @target)
                  maxrange))

(defmethod effect-useful? :effects.target/audiovisual
  [_ _effect-ctx _ctx]
  false)

(defn- skill-usable-state
  [{:keys [skill/cooling-down? skill/effects] :as skill}
   entity
   effect-ctx]
  (cond
   cooling-down?
   :cooldown

   (stats/not-enough-mana? (:entity/stats entity) skill)
   :not-enough-mana

   (not (seq (filter #(effect-applicable? % effect-ctx) effects)))
   :invalid-params

   :else
   :usable))

(def info
  {:k->fn {:creature/level (fn [v _ctx]
                              (str "Level: " v))
           :entity/stats (fn [entity-stats _ctx]
                           (stats/format-text entity-stats))
           :effects.target/convert (fn [_ _ctx]
                                    "Converts target to your side.")
           :effects.target/damage (fn [{[min max] :damage/min-max} _ctx]
                                    (str min "-" max " damage"))
           :effects.target/kill (fn [_ _ctx]
                                  "Kills target")
           :effects.target/melee-damage (fn [_ _ctx]
                                          "Damage based on entity strength.")
           :effects.target/spiderweb (fn [_ _ctx]
                                       "Spiderweb slows 50% for 5 seconds.")
           :effects.target/stun (fn [duration _ctx]
                                  (str "Stuns for " (readable/f duration) " seconds"))
           :effects/spawn (fn [{:keys [property/pretty-name]} _ctx]
                            (str "Spawns a " pretty-name))
           :effects/target-all (fn [_ _ctx]
                                 "All visible targets")
           :entity/delete-after-duration (fn [counter {:keys [ctx/elapsed-time]}]
                                             (str "Remaining: " (readable/f (timer-ratio/f elapsed-time counter)) "/1"))
           :entity/faction (fn [faction _ctx]
                             (str "Faction: " (name faction)))
           :entity/fsm (fn [fsm _ctx]
                          (str "State: " (name (:state fsm))))
           :stats/modifiers (fn [mods _ctx]
                              (mods/format-text mods))
           :entity/skills (fn [skills _ctx]
                            (when (seq skills)
                              (str "Skills: " (str/join "," (map name (keys skills))))))
           :entity/species (fn [species _ctx]
                             (str "Creature - " (str/capitalize (name species))))
           :entity/temp-modifier (fn [{:keys [counter]} {:keys [ctx/elapsed-time]}]
                                    (str "Spiderweb - remaining: " (readable/f (timer-ratio/f elapsed-time counter)) "/1"))
           :projectile/piercing? (fn [_ _ctx]
                                   "Piercing")
           :property/pretty-name (fn [v _ctx]
                                    v)
           :skill/cooling-down? (fn [counter {:keys [ctx/elapsed-time]}]
                                   (str "Cooldown: " (readable/f (timer-ratio/f elapsed-time counter)) "/1"))
           :skill/action-time (fn [v _ctx]
                                 (str "Action-Time: " (readable/f v) " seconds"))
           :skill/action-time-modifier-key (fn [v _ctx]
                                              (case v
                                                :stats/cast-speed "Spell"
                                                :stats/attack-speed "Attack"))
           :skill/cooldown (fn [v _ctx]
                             (str "Cooldown: " (readable/f v) " seconds"))
           :skill/cost (fn [v _ctx]
                          (str "Cost: " v " Mana"))
           :maxrange (fn [v _ctx]
                       (str "Range: " v " Meters."))}
   :k-order [:property/pretty-name
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

(defn info-text
  [entity ctx]
  (let [{:keys [k->fn
                k-order
                k->colors]} info
        component-info (fn [[k v]]
                         (let [s (if-let [info-fn (k->fn k)]
                                   (str (info-fn v ctx)))]
                           (if-let [color (k->colors k)]
                             (str "[" color "]" s "[]")
                             s)))]
    (->> entity
         (k-order/sort-by-k-order k-order)
         (keep (fn [{k 0 v 1 :as component}]
                 (str (try (component-info component)
                           (catch Throwable _t
                             (str "*info-error* " k)))
                      (when (map? v)
                        (str "\n" (info-text v ctx))))))
         (str/join "\n")
         remove-newlines)))

(defn- create-component-animation
  [{:keys [animation/frames
           animation/frame-duration
           animation/looping?
           delete-after-stopped?]}
   _ctx]
  (assert (not (and looping? delete-after-stopped?)))
  {:frames (vec frames)
   :frame-duration frame-duration
   :looping? looping?
   :cnt 0
   :maxcnt (* (count frames) (float frame-duration))
   :delete-after-stopped? delete-after-stopped?})

(defn- create-component-body
  [{[x y] :position
    :keys [position
           width
           height
           collides?
           z-order
           rotation-angle]}
   _ctx]
  (assert position)
  (assert width)
  (assert height)
  (assert (>= width  (if collides? minimum-size 0)))
  (assert (>= height (if collides? minimum-size 0)))
  (assert (or (boolean? collides?) (nil? collides?)))
  (assert ((set z-orders) z-order))
  (assert (or (nil? rotation-angle)
              (<= 0 rotation-angle 360)))
  (map->BodyRecord
   {:position (mapv float position)
    :width  (float width)
    :height (float height)
    :collides? collides?
    :z-order z-order
    :rotation-angle (or rotation-angle 0)}))

(defn- create-component-delete-after-duration
  [duration {:keys [ctx/elapsed-time]}]
  (create-timer elapsed-time duration))

(defn- create-component-projectile-collision
  [v _ctx]
  (assoc v :already-hit-bodies #{}))

(defn- create-component-stats
  [v _ctx]
  (-> v
      (update :stats/mana (fn [v] [v v]))
      (update :stats/hp   (fn [v] [v v]))))

(def k->create-component
  {:entity/animation create-component-animation
   :entity/body create-component-body
   :entity/delete-after-duration create-component-delete-after-duration
   :entity/projectile-collision create-component-projectile-collision
   :entity/stats create-component-stats})

(defn create-component
  [ctx k v]
  (if-let [f (k->create-component k)]
    (f v ctx)
    v))

(defmulti create-entity-state
  (fn [[k _v] _eid _ctx]
    k))

(defmethod create-entity-state :default
  [[_k v] _eid _ctx]
  v)

(defmethod create-entity-state :active-skill
  [[_k [skill effect-ctx]] eid {:keys [ctx/elapsed-time]}]
  {:skill skill
   :effect-ctx effect-ctx
   :counter (->> skill
                 :skill/action-time
                 (stats/apply-action-speed-modifier (:entity/stats @eid) skill)
                 (create-timer elapsed-time))})

(defmethod create-entity-state :stunned
  [[_k duration] _eid {:keys [ctx/elapsed-time]}]
  {:counter (create-timer elapsed-time duration)})

(defmethod create-entity-state :player-moving
  [[_k movement-vector] _eid _ctx]
  {:movement-vector movement-vector})

(defmethod create-entity-state :npc-moving
  [[_k movement-vector] eid {:keys [ctx/elapsed-time]}]
  {:movement-vector movement-vector
   :timer (create-timer elapsed-time
                        (* (stats/get-value (:entity/stats @eid) :stats/reaction-time)
                           0.016))})

(defmethod create-entity-state :player-item-on-cursor
  [[_k item] _eid _ctx]
  {:item item})

(def fsms
  {:npc (fsm/fsm-inc
          [[:npc-sleeping
            :kill -> :npc-dead
            :stun -> :stunned
            :alert -> :npc-idle]
           [:npc-idle
            :kill -> :npc-dead
            :stun -> :stunned
            :start-action -> :active-skill
            :movement-direction -> :npc-moving]
           [:npc-moving
            :kill -> :npc-dead
            :stun -> :stunned
            :timer-finished -> :npc-idle]
           [:active-skill
            :kill -> :npc-dead
            :stun -> :stunned
            :action-done -> :npc-idle]
           [:stunned
            :kill -> :npc-dead
            :effect-wears-off -> :npc-idle]
           [:npc-dead]])
   :player (fsm/fsm-inc
            [[:player-idle
              :kill -> :player-dead
              :stun -> :stunned
              :start-action -> :active-skill
              :pickup-item -> :player-item-on-cursor
              :movement-input -> :player-moving]
             [:player-moving
              :kill -> :player-dead
              :stun -> :stunned
              :no-movement-input -> :player-idle]
             [:active-skill
              :kill -> :player-dead
              :stun -> :stunned
              :action-done -> :player-idle]
             [:stunned
              :kill -> :player-dead
              :effect-wears-off -> :player-idle]
             [:player-item-on-cursor
              :kill -> :player-dead
              :stun -> :stunned
              :drop-item -> :player-idle
              :dropped-item -> :player-idle]
             [:player-dead]])})

(defn- create-fsm
  [fsm initial-state]
  (assoc ((case fsm
            :fsms/player (:player fsms)
            :fsms/npc (:npc fsms))
          initial-state
          nil)
         :state initial-state))

(defn- after-create-fsm
  [{:keys [fsm initial-state]} eid ctx]
  [[:tx/assoc eid :entity/fsm (create-fsm fsm initial-state)]
   [:tx/assoc eid initial-state (create-entity-state [initial-state nil] eid ctx)]])

(defn- after-create-skills
  [skills eid _ctx]
  (cons [:tx/assoc eid :entity/skills nil]
        (for [skill skills]
          [:tx/add-skill eid skill])))

(defn- after-create-inventory
  [items eid _ctx]
  (cons [:tx/assoc eid :entity/inventory (->> #:inventory.slot{:bag      [6 4]
                                                               :weapon   [1 1]
                                                               :shield   [1 1]
                                                               :helm     [1 1]
                                                               :chest    [1 1]
                                                               :leg      [1 1]
                                                               :glove    [1 1]
                                                               :boot     [1 1]
                                                               :cloak    [1 1]
                                                               :necklace [1 1]
                                                               :rings    [2 1]}
                                              (map (fn [[slot [width height]]]
                                                     [slot (moon-g2d/create width height (constantly nil))]))
                                              (into {}))]
        (for [item items]
          [:tx/pickup-item eid item])))

(def k->after-create
  {:entity/fsm after-create-fsm
   :entity/inventory after-create-inventory
   :entity/skills after-create-skills})

(defn after-create-component
  [ctx eid [k v]]
  (if-let [f (k->after-create k)]
    (f v eid ctx)
    nil))

(defn- item-place-position [{:keys [ctx/world-mouse-position]} player-entity]
  (assert world-mouse-position)
  (let [player-position (:body/position (:entity/body player-entity))
        maxrange (- (:entity/click-distance-tiles player-entity) 0.1)]
    (v2/add player-position
           (v2/scale (v2/direction player-position world-mouse-position)
                     (min maxrange
                          (v2/distance player-position world-mouse-position))))))

(defn- mouse-position [{:keys [ctx/input]}]
  (input/position input))

(defn- mouseover-actor [{:keys [ctx/stage] :as ctx}]
  (let [[x y] (unproject (:stage/viewport stage) (mouse-position ctx))]
    (stage/hit stage x y true)))

(defn- mouseover-actor-info [actor]
  (let [inventory-slot (and (actor/getParent actor)
                            (= "inventory-cell" (actor/getName (actor/getParent actor)))
                            (actor/getUserObject (actor/getParent actor)))]
    (cond
      inventory-slot
      [:mouseover-actor/inventory-cell inventory-slot]

      (title-bar? actor)
      [:mouseover-actor/window-title-bar]

      (is? actor)
      [:mouseover-actor/button]

      :else
      [:mouseover-actor/unspecified])))

(defn- register-eid! [ctx eid]
  (assert (and (not (contains? @eid :entity/id))))
  (let [id (swap! (:ctx/id-counter ctx) inc)]
    (assert (number? id))
    (swap! eid assoc :entity/id id)
    (swap! (:ctx/entity-ids ctx) assoc id eid))

  (assert (:entity/body @eid))
  (content-grid/update-entity! (:ctx/content-grid ctx) eid)

  (assert (:entity/body @eid))
  (when (:body/collides? (:entity/body @eid))
    (assert (grid/valid-position? (:ctx/grid ctx) (:entity/body @eid) (:entity/id @eid))))
  (set-touched-cells! (:ctx/grid ctx) eid)
  (when (:body/collides? (:entity/body @eid))
    (set-occupied-cells! (:ctx/grid ctx) eid))
  nil)

(defn- state-enter-player-item-on-cursor
  [{:keys [item]} eid]
  [[:tx/assoc eid :entity/item-on-cursor item]])

(defn- state-enter-active-skill
  [{:keys [skill]} eid]
  [[:tx/sound (:skill/start-action-sound skill)]
   [:tx/set-cooldown eid skill]
   [:tx/update eid :entity/stats stats/pay-mana-cost (:skill/cost skill)]])

(defn- state-enter-npc-dead
  [_ eid]
  [[:tx/mark-destroyed eid]])

(defn- state-enter-player-moving
  [{:keys [movement-vector]} eid]
  [[:tx/assoc eid :entity/movement {:direction movement-vector
                                    :speed (or (stats/get-value (:entity/stats @eid) :stats/movement-speed)
                                               0)}]])

(defn- state-enter-player-dead
  [_ _eid]
  [[:tx/sound "bfxr_playerdeath"]
   [:tx/show-modal {:title "YOU DIED - again!"
                    :text "Good luck next time!"
                    :button-text "OK"
                    :on-click (fn [])}]])

(defn- state-enter-npc-moving
  [{:keys [movement-vector]} eid]
  [[:tx/assoc eid :entity/movement {:direction movement-vector
                                    :speed (or (stats/get-value (:entity/stats @eid) :stats/movement-speed)
                                               0)}]])

(def k->state-enter
  {:player-item-on-cursor state-enter-player-item-on-cursor
   :active-skill state-enter-active-skill
   :npc-dead state-enter-npc-dead
   :player-moving state-enter-player-moving
   :player-dead state-enter-player-dead
   :npc-moving state-enter-npc-moving})

(defn- state-exit-player-item-on-cursor
  [_ eid ctx]
  (let [entity @eid]
    (when (:entity/item-on-cursor entity)
      [[:tx/sound "bfxr_itemputground"]
       [:tx/dissoc eid :entity/item-on-cursor]
       [:tx/spawn-item
        (item-place-position ctx entity)
        (:entity/item-on-cursor entity)]])))

(defn- state-exit-player-moving
  [_ eid _ctx]
  [[:tx/dissoc eid :entity/movement]])

(defn- state-exit-npc-sleeping
  [_ eid _ctx]
  [[:tx/spawn-alert (:body/position (:entity/body @eid)) (:entity/faction @eid) 0.2]
   [:tx/add-text-effect eid "[WHITE]!" 1]])

(defn- state-exit-npc-moving
  [_ eid _ctx]
  [[:tx/dissoc eid :entity/movement]])

(def k->state-exit
  {:player-item-on-cursor state-exit-player-item-on-cursor
   :player-moving state-exit-player-moving
   :npc-sleeping state-exit-npc-sleeping
   :npc-moving state-exit-npc-moving})

(def tx-fn-map
  {:tx/add-skill
   (fn [ctx eid {:keys [property/id] :as skill}]
     {:pre [(not (contains? (:entity/skills @eid) id))]}
     [[:tx/update eid :entity/skills assoc id skill]
      (when (:entity/player? @eid)
        [:tx/ui-update-skill eid skill])])

   :tx/add-text-effect
   (fn [{:keys [ctx/elapsed-time]} eid text duration]
     [[:tx/assoc
       eid
       :entity/string-effect
       (if-let [existing (:entity/string-effect @eid)]
         (-> existing
             (update :text str "\n" text)
             (update :counter timer/increment duration))
         {:text text
          :counter (create-timer elapsed-time duration)})]])

   :tx/assoc
   (fn [_ctx eid k value]
     (swap! eid assoc k value)
     nil)

   :tx/assoc-in
   (fn [_ctx eid ks value]
     (swap! eid assoc-in ks value)
     nil)

   :tx/audiovisual
   (fn [{:keys [ctx/db]} position audiovisual]
     (let [{:keys [tx/sound entity/animation]} (if (keyword? audiovisual)
                                                  (db/build db audiovisual)
                                                  audiovisual)]
       [[:tx/sound sound]
        [:tx/spawn-effect
         position
         {:entity/animation (assoc animation :delete-after-stopped? true)}]]))

   :tx/dissoc
   (fn [_ctx eid k]
     (swap! eid dissoc k)
     nil)

   :tx/effect
   (fn [ctx effect-ctx effects]
     (mapcat #(handle-effect % effect-ctx ctx)
             (filter #(effect-applicable? % effect-ctx) effects)))

   :tx/event
   (fn ([ctx eid event]
         (let [fsm (:entity/fsm @eid)
               _ (assert fsm)
               old-state-k (:state fsm)
               new-fsm (fsm/fsm-event fsm event)
               new-state-k (:state new-fsm)]
           (when-not (= old-state-k new-state-k)
             (let [old-state-obj (let [k (:state (:entity/fsm @eid))]
                                   [k (k @eid)])
                   new-state-obj [new-state-k (create-entity-state [new-state-k nil] eid ctx)]]
               [[:tx/assoc eid :entity/fsm new-fsm]
                [:tx/assoc eid new-state-k (new-state-obj 1)]
                [:tx/dissoc eid old-state-k]
                [:tx/state-exit eid old-state-obj]
                [:tx/state-enter eid new-state-obj]]))))
       ([ctx eid event params]
         (let [fsm (:entity/fsm @eid)
               _ (assert fsm)
               old-state-k (:state fsm)
               new-fsm (fsm/fsm-event fsm event)
               new-state-k (:state new-fsm)]
           (when-not (= old-state-k new-state-k)
             (let [old-state-obj (let [k (:state (:entity/fsm @eid))]
                                   [k (k @eid)])
                   new-state-obj [new-state-k (create-entity-state [new-state-k params] eid ctx)]]
               [[:tx/assoc eid :entity/fsm new-fsm]
                [:tx/assoc eid new-state-k (new-state-obj 1)]
                [:tx/dissoc eid old-state-k]
                [:tx/state-exit eid old-state-obj]
                [:tx/state-enter eid new-state-obj]])))))

   :tx/mark-destroyed
   (fn [_ctx eid]
     (swap! eid assoc :entity/destroyed? true)
     nil)

   :tx/move-entity
   (fn [{:keys [ctx/content-grid ctx/grid]} eid]
     (content-grid/update-entity! content-grid eid)
     (remove-from-touched-cells! grid eid)
     (set-touched-cells! grid eid)
     (when (:body/collides? (:entity/body @eid))
       (remove-from-occupied-cells! grid eid)
       (set-occupied-cells! grid eid))
     nil)

   :tx/pickup-item
   (fn [_ctx eid item]
     (assert (item/valid? item))
     (let [[cell cell-item] (inventory/can-pickup-item? (:entity/inventory @eid) item)]
       (assert cell)
       (assert (or (item/stackable? item cell-item)
                   (nil? cell-item)))
       (if (item/stackable? item cell-item)
         (do #_(tx/stack-item ctx eid cell item))
         [[:tx/set-item eid cell item]])))

   :tx/remove-item
   (fn [ctx eid cell]
     (let [entity @eid
           item (get-in (:entity/inventory entity) cell)]
       (assert item)
       [[:tx/assoc-in eid (cons :entity/inventory cell) nil]
        (when (inventory-cell/applies-modifiers? cell)
          [:tx/update eid :entity/stats stats/remove-mods (:stats/modifiers item)])
        (when (:entity/player? @eid)
          [:tx/ui-remove-item eid cell])]))

   :tx/set-cooldown
   (fn [{:keys [ctx/elapsed-time]} eid skill]
     (swap! eid assoc-in [:entity/skills (:property/id skill) :skill/cooling-down?]
            (create-timer elapsed-time (:skill/cooldown skill)))
     nil)

   :tx/set-item
   (fn [ctx eid cell item]
     (let [entity @eid
           inventory (:entity/inventory entity)]
       (assert (and (nil? (get-in inventory cell))
                    (inventory-cell/valid-slot? cell item)))
       [[:tx/assoc-in eid (cons :entity/inventory cell) item]
        (when (inventory-cell/applies-modifiers? cell)
          [:tx/update eid :entity/stats stats/add-mods (:stats/modifiers item)])
        (when (:entity/player? @eid)
          [:tx/ui-set-item eid cell item])]))

   :tx/show-message
   (fn [{:keys [ctx/stage] :as _ctx} message]
     (-> stage
         :stage/root
         (#(group/findActor % "player-message"))
         (actor/setUserObject (atom {:text message :counter 0})))
     nil)

   :tx/show-modal
   (fn [{:keys [ctx/skin ctx/stage] :as _ctx}
        {:keys [title text button-text on-click]}]
     (assert (not (group/findActor (:stage/root stage) "moon.ui.modal-window")))
     (stage/addActor stage
                       (doto (doto (window/new title skin)
    (table-set-opts/set-opts! {:title title
                               :skin skin
                               :table/rows [[{:actor (label/new text skin)}]
                                            [{:actor (doto (text-button/new button-text skin)
                                                        (actor/addListener
                                                         (change-listener/create
                                                          (fn [_event _actor]
                                                            (actor/remove
                                                             (group/findActor (:stage/root stage)
                                                                               "moon.ui.modal-window"))
                                                            (on-click)))))}]]}))
                         (gdx-window/setModal true)
                         (actor/setName "moon.ui.modal-window")
                         (actor/setPosition (/ (viewport/getWorldWidth (:stage/viewport stage)) 2)
                                         (* (viewport/getWorldHeight (:stage/viewport stage)) (/ 3 4)) align/center)))
     nil)

   :tx/sound
   (fn [{:keys [ctx/audio]} sound-name]
     (audio/play! audio sound-name)
     nil)

   :tx/spawn-alert
   (fn [{:keys [ctx/elapsed-time]} position faction duration]
     [[:tx/spawn-effect
       position
       {:entity/alert-friendlies-after-duration
        {:counter (create-timer elapsed-time duration)
         :faction faction}}]])

   :tx/spawn-creature
   (fn [_ctx {:keys [position creature-property components]}]
     (assert creature-property)
     [[:tx/spawn-entity
       (-> creature-property
           (assoc :entity/body
                  (let [{:keys [body/width body/height]} (:entity/body creature-property)]
                    {:position position
                     :width width
                     :height height
                     :collides? true
                     :z-order :z-order/ground}))
           (assoc :entity/destroy-audiovisual :audiovisuals/creature-die)
           (safe-merge components))]])

   :tx/spawn-effect
   (fn [_ctx position components]
     [[:tx/spawn-entity
       (assoc components
              :entity/body {:width 0.5
                            :height 0.5
                            :z-order :z-order/effect
                            :position position})]])

   :tx/spawn-entity
   (fn [ctx entity]
     (let [entity (reduce (fn [m [k v]]
                            (assoc m k (create-component ctx k v)))
                          {}
                          entity)
           entity (merge (map->EntityRecord {}) entity)
           eid (atom entity)]
       (register-eid! ctx eid)
       (mapcat (fn [component]
                 (after-create-component ctx eid component))
               @eid)))

   :tx/spawn-item
   (fn [_ctx position item]
     [[:tx/spawn-entity
       {:entity/body {:position position
                       :width 0.75
                       :height 0.75
                       :z-order :z-order/on-ground}
        :entity/image (:entity/image item)
        :entity/item item
        :entity/clickable {:type :clickable/item
                           :text (:property/pretty-name item)}}]])

   :tx/spawn-line
   (fn [_ctx {:keys [start end duration color thick?]}]
     [[:tx/spawn-effect
       start
       {:entity/line-render {:thick? thick? :end end :color color}
        :entity/delete-after-duration duration}]])

   :tx/spawn-projectile
   (fn [_ctx
        {:keys [position direction faction]}
        {:keys [entity/image
                projectile/max-range
                projectile/speed
                entity-effects
                projectile/size
                projectile/piercing?]}]
     [[:tx/spawn-entity
       {:entity/body {:position position
                      :width size
                      :height size
                      :z-order :z-order/flying
                      :rotation-angle (v2/angle-from-vector direction)}
        :entity/movement {:direction direction :speed speed}
        :entity/image image
        :entity/faction faction
        :entity/delete-after-duration (/ max-range speed)
        :entity/destroy-audiovisual :audiovisuals/hit-wall
        :entity/projectile-collision {:entity-effects entity-effects
                                      :piercing? piercing?}}]])

   :tx/state-enter
   (fn [ctx eid [state-k state-v]]
     (if-let [f (k->state-enter state-k)]
       (f state-v eid)
       nil))

   :tx/state-exit
   (fn [ctx eid [state-k state-v]]
     (if-let [f (k->state-exit state-k)]
       (f state-v eid ctx)
       nil))

   :tx/toggle-inventory-visible
   (fn [{:keys [ctx/stage]}]
     (let [inventory (group/findActor (:stage/root stage) "moon.ui.windows.inventory")]
       (actor/setVisible inventory (not (actor/isVisible inventory)))
       nil))

   :tx/ui-remove-item
   (fn [{:keys [ctx/stage]} _eid cell]
     (-> stage
         :stage/root
         (#(group/findActor % "moon.ui.windows.inventory"))
         (inventory-window/remove-item! cell))
     nil)

   :tx/ui-set-item
   (fn [{:keys [ctx/skin ctx/stage ctx/textures] :as ctx} _eid cell item]
     (-> stage
         :stage/root
         (#(group/findActor % "moon.ui.windows.inventory"))
         (inventory-window/set-item! cell
                        {:texture-region (textures/texture-region textures (:entity/image item))
                         :tooltip-text (item/info-text item)}
                        skin))
     nil)

   :tx/ui-update-skill
   (fn [{:keys [ctx/skin ctx/stage ctx/textures] :as ctx} _eid skill]
     (-> stage
         :stage/root
         (#(group/findActor % "moon.ui.action-bar"))
         (action-bar/add-skill! {:skill-id (:property/id skill)
                          :texture-region (textures/texture-region textures (:entity/image skill))
                          :tooltip-text (info-text skill ctx)}
                         skin))
     nil)

   :tx/unregister-eid
   (fn [{:keys [ctx/content-grid ctx/entity-ids ctx/grid]} eid]
     (let [id (:entity/id @eid)]
       (assert (contains? @entity-ids id))
       (swap! entity-ids dissoc id))
     (content-grid/remove-entity! content-grid eid)
     (remove-from-touched-cells! grid eid)
     (when (:body/collides? (:entity/body @eid))
       (remove-from-occupied-cells! grid eid))
     nil)

   :tx/update
   (fn [_ctx eid & params]
     (apply swap! eid update params)
     nil)})

(defn do!
  [ctx txs]
  (try (actions! tx-fn-map ctx txs)
       (catch Throwable t
         (throw (ex-info "Error handling txs"
                         {:txs txs} t)))))

(def colors
  (let [outline-alpha 0.4]
    {:colors/mouseover-tile-air (color/toFloatBits [1 1 0 0.5])
     :colors/mouseover-tile-none (color/toFloatBits [1 0 0 0.5])
     :colors/debug-body-outline-collides (color/toFloatBits [1 1 1 1])
     :colors/debug-body-outline (color/toFloatBits [0.5 0.5 0.5 1])
     :colors/debug-body-outline-render-error (color/toFloatBits [1 0 0 1])
     :colors/debug-cell-entities (color/toFloatBits [1 0 0 0.6])
     :colors/debug-cell-occupied (color/toFloatBits [0 0 1 0.6])
     :colors/debug-potential-field (fn [ratio]
                                     (color/toFloatBits [ratio (- 1 ratio) ratio 0.6]))
     :colors/target-all-line (color/toFloatBits [1 0 0 0.75])
     :colors/target-all-render (color/toFloatBits [1 0 0 0.5])
     :colors/target-entity-line (color/toFloatBits [1 0 0 0.75])
     :colors/target-entity-in-range (color/toFloatBits [1 0 0 0.5])
     :colors/target-entity-not-in-range (color/toFloatBits [1 1 0 0.5])
     :colors/enemy-color (color/toFloatBits [1 0 0 outline-alpha])
     :colors/friendly-color (color/toFloatBits [0 1 0 outline-alpha])
     :colors/neutral-color (color/toFloatBits [1 1 1 outline-alpha])
     :colors/hp-bar (fn [ratio]
                      (let [ratio (float ratio)
                            color (cond
                                    (> ratio 0.75) :green
                                    (> ratio 0.5) :darkgreen
                                    (> ratio 0.25) :yellow
                                    :else :red)]
                        (color {:green (color/toFloatBits [0 0.8 0 1])
                                :darkgreen (color/toFloatBits [0 0.5 0 1])
                                :yellow (color/toFloatBits [0.5 0.5 0 1])
                                :red (color/toFloatBits [0.5 0 0 1])})))
     :colors/hp-bar-rect (color/toFloatBits [0 0 0 1])
     :colors/temp-modifier (color/toFloatBits [0.5 0.5 0.5 0.4])
     :colors/active-skill-circle (color/toFloatBits [1 1 1 0.125])
     :colors/active-skill-sector (color/toFloatBits [1 1 1 0.5])
     :colors/stunned (color/toFloatBits [1 1 1 0.6])
     :colors/explored-tile (color/toFloatBits [0.5 0.5 0.5 1])
     :colors/visible-tile (color/toFloatBits [1 1 1 1])
     :colors/invisible-tile (color/toFloatBits [0 0 0 1])
     :colors/droppable-item (color/toFloatBits [0 0.6 0 0.8 1])
     :colors/not-allowed-drop-item (color/toFloatBits [0.6 0 0 0.8 1])
     :colors/item-rect (color/toFloatBits [0.5 0.5 0.5 1])}))

(def controls
  {:zoom-in :input.keys/minus
   :zoom-out :input.keys/equals
   :unpause-once :input.keys/p
   :unpause-continously :input.keys/space
   :close-windows-key :input.keys/escape
   :toggle-inventory :input.keys/i
   :toggle-entity-info :input.keys/e
   :open-debug-button :input.buttons/right})

(def controls-info
  (str/join "\n"
            ["[W][A][S][D] - Move"
             "[ESCAPE] - Close windows"
             "[I] - Inventory window"
             "[E] - Entity Info window"
             "[-]/[=] - Zoom"
             "[P]/[SPACE] - Unpause"
             "rightclick on tile or entity - open debug data window"
             "Leftmouse click - use skill/drop item on cursor"]))

(def help-menu-item
  {:label "Help"
   :items [{:label controls-info}]})

(def ctx-data-menu-item
  {:label "Ctx Data"
   :items [{:label "Show data"
            :on-click (fn [{:keys [ctx/skin
                                   ctx/stage] :as ctx}]
                        (stage/addActor stage
                                        (data-viewer-window/create
                                         {:title "Data View"
                                          :data ctx
                                          :width 1000
                                          :height 1000
                                          :skin skin}))
                        ctx)}]})

(def debug-flags-menu-item
  {:label "Debug"
   :items [{:label "Toggle show-tile-grid?"
            :on-click #(update % :ctx/show-tile-grid? not)}
           {:label "Toggle show-cell-entities?"
            :on-click #(update % :ctx/show-cell-entities? not)}
           {:label "Toggle show-cell-occupied?"
            :on-click #(update % :ctx/show-cell-occupied? not)}
           {:label "Toggle show-body-bounds?"
            :on-click #(update % :ctx/show-body-bounds? not)}
           {:label "Potential field colors: off"
            :on-click #(assoc % :ctx/show-potential-field-colors? nil)}
           {:label "Potential field colors: :good"
            :on-click #(assoc % :ctx/show-potential-field-colors? :good)}
           {:label "Potential field colors: :evil"
            :on-click #(assoc % :ctx/show-potential-field-colors? :evil)}]})

(def select-world-menu-item
  {:label "Select World"
   :items (for [[label world-fn] [["Vampire" tmx/vampire]
                                  ["UF Caves" uf-caves/create]
                                  ["Modules" modules/create]]]
            {:label (str "Start " label)
             :on-click (fn [ctx]
                         #_(let [rebuild-actors! nil
                                 #_(fn rebuild-actors! [stage ctx]
                                     (.clear stage)
                                     ((requiring-resolve 'game.create.add-actors/step) ctx))
                                 create-world nil
                                 #_(requiring-resolve 'game.create.world/step)
                                 ui stage
                                 stage (get-stage actor)]
                             (rebuild-actors! ui ctx)
                             #_(disposable/dispose! (:ctx/tiled-map ctx))
                             (set! (.ctx ^Stage stage) (create-world ctx world-fn)))
                         ctx)})})

(def dev-menus
  [ctx-data-menu-item
   debug-flags-menu-item
   help-menu-item
   select-world-menu-item])

(def dev-update-labels
  [{:label "elapsed-time"
    :update-fn (fn [{:keys [ctx/elapsed-time]}]
                 (str (readable/f elapsed-time) " seconds"))
    :icon "images/clock.png"}
   {:label "FPS"
    :update-fn (fn [{:keys [ctx/graphics]}]
                 (graphics/getFramesPerSecond graphics))
    :icon "images/fps.png"}
   {:label "Mouseover-entity id"
    :update-fn (fn [{:keys [ctx/mouseover-eid]}]
                 (when-let [entity (and mouseover-eid @mouseover-eid)]
                   (:entity/id entity)))
    :icon "images/mouseover.png"}
   {:label "paused?"
    :update-fn :ctx/paused?}
   {:label "GUI"
    :update-fn (fn [{:keys [ctx/ui-mouse-position]}]
                 (mapv int ui-mouse-position))}
   {:label "World"
    :update-fn (fn [{:keys [ctx/world-mouse-position]}]
                 (mapv int world-mouse-position))}
   {:label "Zoom"
    :update-fn (fn [{:keys [ctx/world-viewport]}]
                 (orthographic-camera/zoom (viewport/getCamera world-viewport)))
    :icon "images/zoom.png"}])

(def max-speed
  (/ minimum-size max-delta))

(def render-z-order
  (apply hash-map (interleave z-orders (range))))

(def ^:private active-skill-radius
  (let [tile-size 48
        image-width 32]
    (/ (/ image-width tile-size) 2)))

(defn- render-image
  [image {:keys [entity/body]} {:keys [ctx/textures]}]
  [[:draw/texture-region
    (textures/texture-region textures image)
    (:body/position body)
    {:center? true
     :rotation (or (:body/rotation-angle body)
                   0)}]])

(defn- render-clickable
  [{:keys [text]}
   {:keys [entity/body
           entity/mouseover?]}
   _ctx]
  (when (and mouseover? text)
    (let [[x y] (:body/position body)]
      [[:draw/text {:text text
                    :x x
                    :y (+ y (/ (:body/height body) 2))
                    :up? true}]])))

(defn- render-animation
  [{:keys [frames
           cnt
           frame-duration]}
   entity
   ctx]
  (render-image (frames (min (int (/ (float cnt) (float frame-duration)))
                               (dec (count frames))))
                entity
                ctx))

(defn- render-line-render
  [{:keys [thick? end color]}
   {:keys [entity/body]}
   _ctx]
  (let [position (:body/position body)]
    (if thick?
      [[:draw/with-line-width
        4
        [[:draw/line position end color]]]]
      [[:draw/line position end color]])))

(defn- render-mouseover
  [_
   {:keys [entity/body
           entity/faction]}
   {:keys [ctx/colors
           ctx/player-eid]}]
  (let [player @player-eid]
    [[:draw/with-line-width 5
      [[:draw/ellipse
        (:body/position body)
        (/ (:body/width  body) 2)
        (/ (:body/height body) 2)
        (cond (= faction (faction/enemy (:entity/faction player)))
              (:colors/enemy-color colors)
              (= faction (:entity/faction player))
              (:colors/friendly-color colors)
              :else
              (:colors/neutral-color colors))]]]]))

(defn- render-npc-sleeping
  [_ {:keys [entity/body]} _ctx]
  (let [[x y] (:body/position body)]
    [[:draw/text {:text "zzz"
                  :x x
                  :y (+ y (/ (:body/height body) 2))
                  :up? true}]]))

(defn- render-player-item-on-cursor
  [{:keys [item]}
   entity
   {:keys [ctx/textures]
    :as ctx}]
  (when-not (mouseover-actor ctx)
    [[:draw/texture-region
      (textures/texture-region textures (:entity/image item))
      (item-place-position ctx entity)
      {:center? true}]]))

(defn- render-stats
  [_ entity {:keys [ctx/colors]}]
  (let [ratio (ratio (stats/get-hitpoints (:entity/stats entity)))]
    (when (or (< ratio 1) (:entity/mouseover? entity))
      (let [{:keys [body/position body/width body/height]} (:entity/body entity)
            [x y] position
            x (- x (/ width  2))
            y (+ y (/ height 2))
            height (* 5 world-unit-scale)
            border (* 1 world-unit-scale)]
        [[:draw/filled-rectangle x y width height (:colors/hp-bar-rect colors)]
         [:draw/filled-rectangle
          (+ x border)
          (+ y border)
          (- (* width ratio) (* 2 border))
          (- height          (* 2 border))
          ((:colors/hp-bar colors) ratio)]]))))

(defn- render-string-effect
  [{:keys [text]} entity _ctx]
  (let [[x y] (:body/position (:entity/body entity))]
    [[:draw/text {:text text
                  :x x
                  :y (+ y
                        (/ (:body/height (:entity/body entity)) 2)
                        (* 5 world-unit-scale))
                  :scale 2
                  :up? true}]]))

(defn- render-stunned
  [_ {:keys [entity/body]} {:keys [ctx/colors]}]
  [[:draw/circle
    (:body/position body)
    0.5
    (:colors/stunned colors)]])

(defn- render-temp-modifier
  [_ entity {:keys [ctx/colors]}]
  [[:draw/filled-circle
    (:body/position (:entity/body entity))
    0.5
    (:colors/temp-modifier colors)]])

(defmulti effect-render
  (fn [[k _v] _effect-ctx _ctx]
    k))

(defmethod effect-render :default
  [_ _effect-ctx _ctx]
  nil)

(defmethod effect-render :effects/target-all
  [_
   {:keys [effect/source]}
   {:keys [ctx/active-entities
           ctx/colors
           ctx/raycaster]}]
  (let [source* @source]
    (for [target* (map deref (affected-targets active-entities raycaster source*))]
      [:draw/line
       (:body/position (:entity/body source*))
       (:body/position (:entity/body target*))
       (:colors/target-all-render colors)])))

(defmethod effect-render :effects/target-entity
  [[_ {:keys [maxrange]}]
   {:keys [effect/source effect/target]}
   {:keys [ctx/colors]}]
  (when target
    (let [body        (:entity/body @source)
          target-body (:entity/body @target)]
      [[:draw/line
        (body/start-point body target-body)
        (body/end-point body target-body maxrange)
        (if (body/in-range? body target-body maxrange)
          (:colors/target-entity-in-range colors)
          (:colors/target-entity-not-in-range colors))]])))

(defn- render-active-skill
  [{:keys [skill effect-ctx counter]}
   entity
   {:keys [ctx/colors
           ctx/elapsed-time
           ctx/textures]
    :as ctx}]
  (let [{:keys [entity/image skill/effects]} skill
        radius active-skill-radius]
    (concat (let [action-counter-ratio (timer-ratio/f elapsed-time counter)
                  texture-region (textures/texture-region textures image)
                  [x y] (:body/position (:entity/body entity))
                  y (+ (float y)
                       (float (/ (:body/height (:entity/body entity)) 2))
                       (float 0.15))
                  center [x (+ y radius)]]
              [[:draw/filled-circle center radius (:colors/active-skill-circle colors)]
               [:draw/sector
                center
                radius
                (math/to-radians 90)
                (math/to-radians (* (float action-counter-ratio) 360))
                (:colors/active-skill-sector colors)]
               [:draw/texture-region texture-region [(- (float x) radius) y]]])
            (mapcat #(effect-render % effect-ctx ctx)
                    effects))))

(defn tile-color-setter*
  [{:keys [ray-blocked?
           explored-tile-corners
           light-position
           see-all-tiles?
           explored-tile-color
           visible-tile-color
           invisible-tile-color]}]
  #_(reset! do-once false)
  (let [light-cache (atom {})]
    (fn tile-color-setter [_color x y]
      (let [position [(int x) (int y)]
            explored? (get @explored-tile-corners position) ; TODO needs int call ?
            base-color (if explored?
                         explored-tile-color
                         invisible-tile-color)
            cache-entry (get @light-cache position :not-found)
            blocked? (if (= cache-entry :not-found)
                       (let [blocked? (ray-blocked? light-position position)]
                         (swap! light-cache assoc position blocked?)
                         blocked?)
                       cache-entry)]
        #_(when @do-once
            (swap! ray-positions conj position))
        (if blocked?
          (if see-all-tiles?
            visible-tile-color
            base-color)
          (do (when-not explored?
                (swap! explored-tile-corners assoc (mapv int position) true))
              visible-tile-color))))))

(declare draw!)

(def ^:private draw-fns
  {:draw/circle (fn [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
                  (shape-drawer/setColor shape-drawer color-float-bits)
                  (shape-drawer/circle shape-drawer x y radius))
   :draw/ellipse (fn [{:keys [ctx/shape-drawer]} [x y] radius-x radius-y color-float-bits]
                   (shape-drawer/setColor shape-drawer color-float-bits)
                   (shape-drawer/ellipse shape-drawer x y radius-x radius-y))
   :draw/filled-circle (fn [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
                          (shape-drawer/setColor shape-drawer color-float-bits)
                          (shape-drawer/filledCircle shape-drawer x y radius))
   :draw/filled-rectangle (fn [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
                             (shape-drawer/setColor shape-drawer color-float-bits)
                             (shape-drawer/filledRectangle shape-drawer x y w h))
   :draw/grid (fn [ctx leftx bottomy gridw gridh cellw cellh color-float-bits]
                (let [w (* (float gridw) (float cellw))
                      h (* (float gridh) (float cellh))
                      topy (+ (float bottomy) (float h))
                      rightx (+ (float leftx) (float w))]
                  (doseq [idx (range (inc (float gridw)))
                          :let [linex (+ (float leftx) (* (float idx) (float cellw)))]]
                    (draw! ctx
                           [[:draw/line [linex topy] [linex bottomy] color-float-bits]]))
                  (doseq [idx (range (inc (float gridh)))
                          :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
                    (draw! ctx
                           [[:draw/line [leftx liney] [rightx liney] color-float-bits]]))))
   :draw/line (fn [{:keys [ctx/shape-drawer]} [sx sy] [ex ey] color-float-bits]
                 (shape-drawer/setColor shape-drawer color-float-bits)
                 (shape-drawer/line shape-drawer sx sy ex ey))
   :draw/rectangle (fn [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
                      (shape-drawer/setColor shape-drawer color-float-bits)
                      (shape-drawer/rectangle shape-drawer x y w h))
   :draw/sector (fn [{:keys [ctx/shape-drawer]} [center-x center-y] radius start-radians radians color-float-bits]
                   (shape-drawer/setColor shape-drawer color-float-bits)
                   (shape-drawer/sector shape-drawer center-x center-y radius start-radians radians))
   :draw/text (fn [{:keys [ctx/batch
                            ctx/unit-scale
                            ctx/default-font]}
                     {:keys [font scale x y text up?]}]
                 (let [font (or font default-font)
                       unit-scale @unit-scale
                       scale (or scale 1)
                       font-data (bitmap-font/getData font)
                       old-scale (bitmap-font-data/scaleX font-data)
                       target-width 0
                       wrap? false
                       scale (* (float unit-scale)
                                (float scale))]
                   (bitmap-font-data/setScale font-data (* old-scale scale))
                   (bitmap-font/draw font
                                      batch
                                      text
                                      x
                                      (+ y (if up?
                                             (-> text
                                                 (str/split #"\n")
                                                 count
                                                 (* (bitmap-font/getLineHeight font)))
                                             0))
                                      target-width
                                      align/center
                                      wrap?)
                   (bitmap-font-data/setScale font-data old-scale)))
   :draw/texture-region (fn [{:keys [ctx/batch
                                      ctx/unit-scale]}
                               texture-region
                               [x y]
                               & {:keys [center? rotation]}]
                           (let [[w h] (let [dimensions [(texture-region/getRegionWidth texture-region)
                                                         (texture-region/getRegionHeight texture-region)]]
                                          (if (= @unit-scale 1)
                                            dimensions
                                            (mapv (comp float (partial * world-unit-scale))
                                                  dimensions)))]
                             (if center?
                               (batch/draw batch
                                           texture-region
                                           (- (float x) (/ (float w) 2))
                                           (- (float y) (/ (float h) 2))
                                           (/ (float w) 2)
                                           (/ (float h) 2)
                                           w
                                           h
                                           1
                                           1
                                           (or rotation 0))
                               (batch/draw batch texture-region x y w h))))
   :draw/with-line-width (fn [{:keys [ctx/shape-drawer]
                                :as ctx}
                               width
                               draws]
                           (let [old-line-width (shape-drawer/getDefaultLineWidth shape-drawer)]
                             (shape-drawer/setDefaultLineWidth shape-drawer (* width old-line-width))
                             (draw! ctx draws)
                             (shape-drawer/setDefaultLineWidth shape-drawer old-line-width)))})

(defn draw!
  [ctx draws]
  (doseq [{k 0 :as component} draws
          :when component]
    (apply (get draw-fns k) ctx (rest component))))

(def k->render
  {:entity/clickable render-clickable
   :player-item-on-cursor render-player-item-on-cursor
   :entity/animation render-animation
   :entity/image render-image
   :entity/line-render render-line-render
   :entity/mouseover? render-mouseover
   :entity/stats render-stats
   :entity/string-effect render-string-effect
   :entity/temp-modifier render-temp-modifier
   :active-skill render-active-skill
   :npc-sleeping render-npc-sleeping
   :stunned render-stunned})

(defn draw-component
  [ctx entity k v]
  ((k->render k) v entity ctx))

(defn action-bar-create [_ctx]
  (doto (doto (table/new)
              (table-set-opts/set-opts! {:table/cell-defaults {:pad 2}
                                         :table/rows [[{:actor (doto (horizontal-group/new)
                                                                    (horizontal-group/space 2)
                                                                    (horizontal-group/pad 2)
                                                                    (actor/setName "moon.ui.action-bar.horizontal-group")
                                                                    (actor/setUserObject (doto (button-group/new)
                                                                                           (button-group/setMaxCheckCount 1)
                                                                                           (button-group/setMinCheckCount 0))))
                                                       :expand? true
                                                       :bottom? true}]]}))
    (layout/setFillParent true)
    (actor/setName "moon.ui.action-bar")))

(defn hp-mana-bar-create
  [{:keys [ctx/textures
           ctx/stage]}]
  (let [{:keys [rahmen-file
                rahmenw
                rahmenh
                hpcontent-file
                manacontent-file
                y-mana]} {:rahmen-file "images/rahmen.png"
                          :rahmenw 150
                          :rahmenh 26
                          :hpcontent-file "images/hp.png"
                          :manacontent-file "images/mana.png"
                          :y-mana 80}
        [x y-mana] [(/ (viewport/getWorldWidth (:stage/viewport stage)) 2)
                    y-mana]
        rahmen-tex-reg (textures/texture-region textures {:image/file rahmen-file})
        y-hp (+ y-mana rahmenh)
        render-hpmana-bar (fn [x y content-file minmaxval name]
                            [[:draw/texture-region rahmen-tex-reg [x y]]
                             [:draw/texture-region
                              (textures/texture-region textures
                                                       {:image/file content-file
                                                        :image/bounds [0 0 (* rahmenw (ratio minmaxval)) rahmenh]})
                              [x y]]
                             [:draw/text {:text (str (readable/f (minmaxval 0))
                                                     "/"
                                                     (minmaxval 1)
                                                     " "
                                                     name)
                                          :x (+ x 75)
                                          :y (+ y 2)
                                          :up? true}]])
        create-draws (fn [{:keys [ctx/player-eid]}]
                       (let [stats (:entity/stats @player-eid)
                             x (- x (/ rahmenw 2))]
                         (concat
                          (render-hpmana-bar x y-hp hpcontent-file (stats/get-hitpoints stats) "HP")
                          (render-hpmana-bar x y-mana manacontent-file (stats/get-mana stats) "MP"))))]
    (actor/new
     (fn [_actor _delta])
     (fn [this _batch _parent-alpha]
       (when-let [stage (actor/getStage this)]
         (draw! (:stage/ctx stage)
                (create-draws (:stage/ctx stage))))))))

(defn- clicked-inventory-cell-player-idle
  [eid cell]
  (when-let [item (get-in (:entity/inventory @eid) cell)]
    [[:tx/sound "bfxr_takeit"]
     [:tx/event eid :pickup-item item]
     [:tx/remove-item eid cell]]))

(defn- clicked-inventory-cell-player-item-on-cursor
  [eid cell]
  (let [entity @eid
        inventory (:entity/inventory entity)
        item-in-cell (get-in inventory cell)
        item-on-cursor (:entity/item-on-cursor entity)]
    (cond
     (and (not item-in-cell)
          (inventory-cell/valid-slot? cell item-on-cursor))
     [[:tx/sound "bfxr_itemput"]
      [:tx/dissoc eid :entity/item-on-cursor]
      [:tx/set-item eid cell item-on-cursor]
      [:tx/event eid :dropped-item]]

     (and item-in-cell
          (item/stackable? item-in-cell item-on-cursor))
     [[:tx/sound "bfxr_itemput"]
      [:tx/dissoc eid :entity/item-on-cursor]
      [:tx/stack-item eid cell item-on-cursor]
      [:tx/event eid :dropped-item]]

     (and item-in-cell
          (inventory-cell/valid-slot? cell item-on-cursor))
     [[:tx/sound "bfxr_itemput"]
      [:tx/dissoc eid :entity/item-on-cursor]
      [:tx/remove-item eid cell]
      [:tx/set-item eid cell item-on-cursor]
      [:tx/event eid :dropped-item]
      [:tx/event eid :pickup-item item-in-cell]])))

(def k->clicked-inventory-cell
  {:player-item-on-cursor clicked-inventory-cell-player-item-on-cursor
   :player-idle clicked-inventory-cell-player-idle})

(defn handle-clicked-inventory-cell
  [player-eid cell]
  (let [state-k (:state (:entity/fsm @player-eid))]
    (when-let [handler (k->clicked-inventory-cell state-k)]
      (handler player-eid cell))))

(defn inventory-window-create
  [{:keys [ctx/colors
           ctx/skin
           ctx/stage
           ctx/textures]}]
  (let [slot->y-sprite-idx #:inventory.slot {:weapon 0
                                             :shield 1
                                             :rings 2
                                             :necklace 3
                                             :helm 4
                                             :cloak 5
                                             :chest 6
                                             :leg 7
                                             :glove 8
                                             :boot 9
                                             :bag 10}
        slot->texture-region (fn [slot]
                               (let [width 48
                                     height 48
                                     sprite-x 21
                                     sprite-y (+ (slot->y-sprite-idx slot) 2)
                                     bounds [(* sprite-x width)
                                             (* sprite-y height)
                                             width
                                             height]]
                                 (textures/texture-region textures
                                                          {:image/file "images/items.png"
                                                           :image/bounds bounds})))]
    (inventory-window-build
     {:do! do!
      :draw! draw!
      :on-click-cell handle-clicked-inventory-cell
      :item-rect-color (:colors/item-rect colors)
      :droppable-item-color (:colors/droppable-item colors)
      :not-allowed-drop-item-color (:colors/not-allowed-drop-item colors)
      :skin skin
      :position [(viewport/getWorldWidth (:stage/viewport stage))
                 (viewport/getWorldHeight (:stage/viewport stage))]
      :slot->texture-region slot->texture-region
      :cell-size 48})))

(defn windows-create [ctx actor-fns]
  (let [group* (group/new)]
    (run! #(group/addActor group* %) (for [f actor-fns] (f ctx)))
    (doto group*
      (actor/setName "moon.ui.windows"))))

(defn stage-dev-menu-create
  [{:keys [ctx/skin
           ctx/textures]}]
  (dev-menu/create
   {:menus dev-menus
    :update-labels (for [item dev-update-labels]
                     (if (:icon item)
                       (update item :icon #(get textures %))
                       item))
    :skin skin}))

(defn stage-info-window-create
  [{:keys [ctx/skin
           ctx/stage]}]
  (info-window/create
   {:title "Entity Info"
    :actor-name "moon.ui.windows.entity-info"
    :visible? false
    :position [(viewport/getWorldWidth (:stage/viewport stage)) 0]
    :set-label-text! (fn [{:keys [ctx/mouseover-eid]
                           :as ctx}]
                       (if-let [eid mouseover-eid]
                         (info-text (apply dissoc @eid [:entity/skills
                                                        :entity/faction
                                                        :active-skill])
                                    ctx)
                         ""))
    :skin skin}))

(defmulti entity-state-draw-ui-view
  (fn [[k _v] _eid _ctx]
    k))

(defmethod entity-state-draw-ui-view :default
  [_ _eid _ctx]
  nil)

(defmethod entity-state-draw-ui-view :player-item-on-cursor
  [_ eid {:keys [ctx/textures
                 ctx/ui-mouse-position]
          :as ctx}]
  (when (mouseover-actor ctx)
    [[:draw/texture-region
      (textures/texture-region textures (:entity/image (:entity/item-on-cursor @eid)))
      ui-mouse-position
      {:center? true}]]))

(defn player-state-draw-create [_ctx]
  (actor/new
   (fn [_actor _delta])
   (fn [this _batch _parent-alpha]
     (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (actor/getStage this))
           entity @player-eid
           state-k (:state (:entity/fsm entity))]
       (draw! ctx (entity-state-draw-ui-view [state-k (state-k entity)] player-eid ctx))))))

(defn player-message-actor-create [_ctx]
  (let [message-duration-seconds 0.5]
    (doto (actor/new
           (fn [this delta]
             (let [state (actor/getUserObject this)]
               (when (:text @state)
                 (swap! state update :counter + delta)
                 (when (>= (:counter @state) message-duration-seconds)
                   (reset! state nil)))))
           (fn [this _batch _parent-alpha]
             (when-let [stage (actor/getStage this)]
               (draw! (:stage/ctx stage)
                      [(let [state (actor/getUserObject this)
                             vp-width (viewport/getWorldWidth (:stage/viewport stage))
                             vp-height (viewport/getWorldHeight (:stage/viewport stage))]
                         (when-let [text (:text @state)]
                           [:draw/text {:x (/ vp-width 2)
                                        :y (+ (/ vp-height 2) 200)
                                        :text text
                                        :scale 2.5
                                        :up? true}]))]))))
      (actor/setName "player-message")
      (actor/setUserObject (atom nil)))))

(defn- player-movement-vector [{:keys [ctx/input]}]
  (let [r (when (input/key-pressed? input :input.keys/d) [1  0])
        l (when (input/key-pressed? input :input.keys/a) [-1 0])
        u (when (input/key-pressed? input :input.keys/w) [0  1])
        d (when (input/key-pressed? input :input.keys/s) [0 -1])]
    (when (or r l u d)
      (let [v (v2/normalise (reduce v2/add [0 0] (remove nil? [r l u d])))]
        (when (pos? (v2/length v))
          v)))))

(defn- interaction-state->txs [[k params] stage player-eid]
  (case k
    :interaction-state/mouseover-actor nil

    :interaction-state/clickable-mouseover-eid
    (let [{:keys [clicked-eid
                  in-click-range?]} params]
      (if in-click-range?
        (case (:type (:entity/clickable @clicked-eid))
          :clickable/player
          [[:tx/toggle-inventory-visible]]

          :clickable/item
          (let [item (:entity/item @clicked-eid)]
            (cond
             (-> stage
                 :stage/root
                 (#(group/findActor % "moon.ui.windows.inventory"))
                 actor/isVisible)
             [[:tx/sound "bfxr_takeit"]
              [:tx/mark-destroyed clicked-eid]
              [:tx/event player-eid :pickup-item item]]

             (inventory/can-pickup-item? (:entity/inventory @player-eid) item)
             [[:tx/sound "bfxr_pickup"]
              [:tx/mark-destroyed clicked-eid]
              [:tx/pickup-item player-eid item]]

             :else
             [[:tx/sound "bfxr_denied"]
              [:tx/show-message "Your Inventory is full"]])))
        [[:tx/sound "bfxr_denied"]
         [:tx/show-message "Too far away"]]))

    :interaction-state.skill/usable
    (let [[skill effect-ctx] params]
      [[:tx/event player-eid :start-action [skill effect-ctx]]])

    :interaction-state.skill/not-usable
    (let [state params]
      [[:tx/sound "bfxr_denied"]
       [:tx/show-message (case state
                           :cooldown "Skill is still on cooldown"
                           :not-enough-mana "Not enough mana"
                           :invalid-params "Cannot use this here")]])

    :interaction-state/no-skill-selected
    [[:tx/sound "bfxr_denied"]
     [:tx/show-message "No selected skill"]]))

(defn- handle-input-player-idle
  [player-eid {:keys [ctx/input
                      ctx/interaction-state
                      ctx/stage]
               :as ctx}]
  (if-let [movement-vector (player-movement-vector ctx)]
    [[:tx/event player-eid :movement-input movement-vector]]
    (when (input/button-just-pressed? input :input.buttons/left)
      (interaction-state->txs interaction-state
                              stage
                              player-eid))))

(defn- handle-input-player-moving
  [eid ctx]
  (if-let [movement-vector (player-movement-vector ctx)]
    [[:tx/assoc eid :entity/movement {:direction movement-vector
                                      :speed (or (stats/get-value (:entity/stats @eid) :stats/movement-speed)
                                                 0)}]]
    [[:tx/event eid :no-movement-input]]))

(defn- handle-input-player-item-on-cursor
  [eid ctx]
  (when (and (input/button-just-pressed? (:ctx/input ctx) :input.buttons/left)
             (not (mouseover-actor ctx)))
    [[:tx/event eid :drop-item]]))

(def k->handle-input
  {:player-idle handle-input-player-idle
   :player-moving handle-input-player-moving
   :player-item-on-cursor handle-input-player-item-on-cursor})

(defn player-effect-ctx [mouseover-eid world-mouse-position player-eid]
  (let [target-position (or (and mouseover-eid
                                 (:body/position (:entity/body @mouseover-eid)))
                            world-mouse-position)]
    {:effect/source player-eid
     :effect/target mouseover-eid
     :effect/target-position target-position
     :effect/target-direction (v2/direction (:body/position (:entity/body @player-eid))
                                         target-position)}))

(defn- choose-skill [ctx entity effect-ctx]
  (->> entity
       :entity/skills
       vals
       (sort-by :skill/cost)
       reverse
       (filter #(and (= :usable (skill-usable-state % entity effect-ctx))
                     (->> (:skill/effects %)
                          (filter (fn [e] (effect-applicable? e effect-ctx)))
                          (some (fn [e] (effect-useful? e effect-ctx ctx))))))
       first))

(defn- create-effect-ctx
  [{:keys [ctx/grid
           ctx/raycaster]}
   eid]
  (let [entity @eid
        target (grid/nearest-enemy grid entity)
        target (when (and target
                          (raycaster/line-of-sight? raycaster entity @target))
                 target)]
    {:effect/source eid
     :effect/target target
     :effect/target-direction (when target
                                (body/direction (:entity/body entity)
                                                (:entity/body @target)))}))

(defn- update-effect-ctx
  [raycaster {:keys [effect/source effect/target] :as effect-ctx}]
  (if (and target
           (not (:entity/destroyed? @target))
           (raycaster/line-of-sight? raycaster @source @target))
    effect-ctx
    (dissoc effect-ctx :effect/target)))

(def k->tick
  {:entity/animation
   (fn [{:keys [delete-after-stopped?
                looping?
                cnt
                maxcnt]
         :as animation}
        eid
        {:keys [ctx/delta-time]}]
     [[:tx/assoc eid :entity/animation (let [maxcnt (float maxcnt)
                                               newcnt (+ (float cnt) (float delta-time))]
                                         (assoc animation :cnt (cond (< newcnt maxcnt) newcnt
                                                                     looping? (min maxcnt (- newcnt maxcnt))
                                                                     :else maxcnt)))]
      (when (and delete-after-stopped?
                 (and (not looping?) (>= cnt maxcnt)))
        [:tx/mark-destroyed eid])])

   :entity/alert-friendlies-after-duration
   (fn [{:keys [counter faction]}
        eid
        {:keys [ctx/elapsed-time
                ctx/grid]}]
     (when (stopped? elapsed-time counter)
       (cons [:tx/mark-destroyed eid]
             (for [friendly-eid (->> {:position (:body/position (:entity/body @eid))
                                      :radius 4}
                                     (grid/circle->entities grid)
                                     (filter #(= (:entity/faction @%) faction)))]
               [:tx/event friendly-eid :alert]))))

   :entity/string-effect
   (fn [{:keys [counter]}
        eid
        {:keys [ctx/elapsed-time]}]
     (when (stopped? elapsed-time counter)
       [[:tx/dissoc eid :entity/string-effect]]))

   :entity/skills
   (fn [skills eid {:keys [ctx/elapsed-time]}]
     (for [{:keys [skill/cooling-down?] :as skill} (vals skills)
           :when (and cooling-down?
                      (stopped? elapsed-time cooling-down?))]
       [:tx/assoc-in eid [:entity/skills (:property/id skill) :skill/cooling-down?] false]))

   :entity/temp-modifier
   (fn [{:keys [modifiers counter]}
        eid
        {:keys [ctx/elapsed-time]}]
     (when (stopped? elapsed-time counter)
       [[:tx/dissoc eid :entity/temp-modifier]
        [:tx/update eid :entity/stats stats/remove-mods modifiers]]))

   :entity/projectile-collision
   (fn [{:keys [entity-effects already-hit-bodies piercing?]}
        eid
        {:keys [ctx/grid]}]
     (let [entity @eid
           cells* (map deref (moon-g2d/get-cells grid (body/touched-tiles (:entity/body entity))))
           hit-entity (first (filter #(and (not (contains? already-hit-bodies %))
                                           (not= (:entity/faction entity)
                                                 (:entity/faction @%))
                                           (:body/collides? (:entity/body @%))
                                           (body/overlaps? (:entity/body entity)
                                                      (:entity/body @%)))
                                     (grid/entities cells*)))
           destroy? (or (and hit-entity (not piercing?))
                        (some #(cell/blocked? % (:body/z-order (:entity/body entity))) cells*))]
       [(when destroy?
          [:tx/mark-destroyed eid])
        (when hit-entity
          [:tx/assoc-in
           eid
           [:entity/projectile-collision
            :already-hit-bodies]
           (conj already-hit-bodies hit-entity)])
        (when hit-entity
          [:tx/effect
           {:effect/source eid
            :effect/target hit-entity}
           entity-effects])]))

   :active-skill
   (fn [{:keys [skill effect-ctx counter]}
        eid
        {:keys [ctx/elapsed-time
                ctx/raycaster]}]
     (let [effect-ctx (update-effect-ctx raycaster effect-ctx)]
       (cond
        (not (seq (filter #(effect-applicable? % effect-ctx)
                          (:skill/effects skill))))
        [[:tx/event eid :action-done]]

        (stopped? elapsed-time counter)
        [[:tx/effect effect-ctx (:skill/effects skill)]
         [:tx/event eid :action-done]])))

   :entity/delete-after-duration
   (fn [counter eid {:keys [ctx/elapsed-time]}]
     (when (stopped? elapsed-time counter)
       [[:tx/mark-destroyed eid]]))

   :stunned
   (fn [{:keys [counter]} eid {:keys [ctx/elapsed-time]}]
     (when (stopped? elapsed-time counter)
       [[:tx/event eid :effect-wears-off]]))

   :npc-moving
   (fn [{:keys [timer]} eid {:keys [ctx/elapsed-time]}]
     (when (stopped? elapsed-time timer)
       [[:tx/event eid :timer-finished]]))

   :npc-sleeping
   (fn [_ eid {:keys [ctx/grid]}]
     (let [entity @eid]
       (when-let [distance (grid/nearest-enemy-distance grid entity)]
         (when (<= distance (stats/get-value (:entity/stats entity) :stats/aggro-range))
           [[:tx/event eid :alert]]))))

   :npc-idle
   (fn [_ eid ctx]
     (let [effect-ctx (create-effect-ctx ctx eid)]
       (if-let [skill (choose-skill ctx @eid effect-ctx)]
         [[:tx/event eid :start-action [skill effect-ctx]]]
         [[:tx/event eid :movement-direction (or (grid/find-direction (:ctx/grid ctx) eid)
                                                 [0 0])]])))

   :entity/movement
   (fn [{:keys [direction
                speed
                rotate-in-movement-direction?]
         :as movement}
        eid
        {:keys [ctx/delta-time
                ctx/grid
                ctx/max-speed]}]
     (assert (<= 0 speed max-speed)
             (pr-str speed))
     (assert (vector? direction))
     (assert (or (zero? (v2/length direction))
                 (number/nearly-equal? 1 (v2/length direction)))
             (str "cannot understand direction: " (pr-str direction)))
     (when-not (or (zero? (v2/length direction))
                   (nil? speed)
                   (zero? speed))
       (let [movement (assoc movement :delta-time delta-time)
             body (:entity/body @eid)]
         (when-let [body (if (:body/collides? body)
                            (try-move-solid-body/f grid body (:entity/id @eid) movement)
                            (update body :body/position v2/move movement))]
           [[:tx/assoc-in eid [:entity/body :body/position] (:body/position body)]
            (when rotate-in-movement-direction?
              [:tx/assoc-in eid [:entity/body :body/rotation-angle] (v2/angle-from-vector direction)])
            [:tx/move-entity eid]]))))})

(defn tick-component
  [ctx eid [k v]]
  (if-let [f (k->tick k)]
    (f v eid ctx)
    nil))

(defn create-bootstrap [application]
  {:ctx/audio    (application/get-audio    application)
   :ctx/files    (application/get-files    application)
   :ctx/graphics (application/get-graphics application)
   :ctx/input    (application/get-input    application)
   :ctx/unit-scale (atom 1)
   :ctx/active-entities nil
   :ctx/delta-time nil
   :ctx/ui-mouse-position nil
   :ctx/world-mouse-position nil
   :ctx/mouseover-eid nil
   :ctx/paused? false
   :ctx/elapsed-time 0
   :ctx/potential-field-cache (atom nil)
   :ctx/id-counter (atom 0)
   :ctx/entity-ids (atom {})
   :ctx/show-potential-field-colors? nil
   :ctx/show-cell-entities? false
   :ctx/show-cell-occupied? false
   :ctx/show-body-bounds? false
   :ctx/show-tile-grid? false})

(defn create-batch [ctx]
  (assoc ctx :ctx/batch (sprite-batch/new)))

(defn create-audio [ctx]
  (assoc ctx
         :ctx/audio (audio/create (:ctx/audio ctx) (:ctx/files ctx))))

(defn create-shape-drawer-texture [ctx]
  (let [pixmap (doto (pixmap/new 1 1 pixmap/rgba8888)
                 (pixmap/set-color! 1 1 1 1)
                 (pixmap/draw-pixel! 0 0))
        texture (texture/new (pixmap-texture-data/new pixmap
                                                      (pixmap/get-format pixmap)
                                                      false
                                                      false))]
    (disposable/dispose! pixmap)
    (assoc ctx :ctx/shape-drawer-texture texture)))

(defn create-shape-drawer [ctx]
  (assoc ctx
         :ctx/shape-drawer (shape-drawer/new (:ctx/batch ctx)
                                             (texture-region/new (:ctx/shape-drawer-texture ctx) 1 0 1 1))))

(defn create-skin [{:keys [ctx/files] :as ctx}]
  (let [skin (skin/new (files/internal files "skin/uiskin.json"))]
    (-> skin
        (skin/getFont "default-font")
        bitmap-font/getData
        (bitmap-font-data/set-markupEnabled true))
    (assoc ctx :ctx/skin skin)))

(defn create-stage [{:keys [ctx/input
                            ctx/batch] :as ctx}]
  (let [stage* (moon-stage/create (fit-viewport/new 1440 900) batch)]
    (input/set-processor! input stage*)
    (assoc ctx :ctx/stage stage*)))

(defn create-init-tooltip [ctx]
  (tooltip-manager/setInitialTime (tooltip-manager/getInstance) 0)
  (colors/put "PRETTY_NAME" (color/new [0.84 0.8 0.52 1]))
  ctx)

(defn create-cursors [ctx]
  (assoc ctx
         :ctx/cursors (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
                         (update-vals data
                                      (fn [[path-segment [hotspot-x hotspot-y]]]
                                        (let [path (format path-format path-segment)
                                              pixmap* (pixmap/new (files/internal (:ctx/files ctx) path))
                                              cursor (graphics/newCursor (:ctx/graphics ctx) pixmap* hotspot-x hotspot-y)]
                                          (disposable/dispose! pixmap*)
                                          cursor))))))

(defn create-textures [{:keys [ctx/files] :as ctx}]
  (assoc ctx :ctx/textures (textures/create files {:folder "resources/"
                                                   :extensions #{"png" "bmp"}})))

(defn create-world-viewport [ctx]
  (assoc ctx
         :ctx/world-viewport (let [world-width (* 1440 world-unit-scale)
                                   world-height (* 900 world-unit-scale)]
                               (fit-viewport/new world-width
                                                    world-height
                                                    (doto (orthographic-camera/new)
                                                      (orthographic-camera/set-to-ortho! false world-width world-height))))))

(defn create-default-font [ctx]
  (assoc ctx
         :ctx/default-font (let [{:keys [path
                                         size
                                         quality-scaling
                                         use-integer-positions?]} {:path "fonts/films.EXL_____.ttf"
                                                                   :size 16
                                                                   :quality-scaling 2
                                                                   :use-integer-positions? false}
                                 generator (font-generator/new (files/internal (:ctx/files ctx) path))
                                 parameter {
                                            :set-size (* size quality-scaling)
                                            :set-min-filter texture-filter/Linear
                                            :set-mag-filter texture-filter/Linear
                                            }
                                 font (font-generator/generate-font generator parameter)
                                 font-data (bitmap-font/getData font)]
                             (disposable/dispose! generator)
                             (bitmap-font-data/setScale font-data (/ quality-scaling))
                             (bitmap-font-data/set-markupEnabled font-data true)
                             (bitmap-font/setUseIntegerPositions font use-integer-positions?)
                             font)))

(defn create-context [ctx]
  (merge (map->R {}) ctx))

(defn create-game-config [ctx]
  (-> ctx
      (assoc :ctx/controls controls)
      (assoc :ctx/controls-info controls-info)
      (assoc :ctx/colors colors)
      (assoc :ctx/render-z-order render-z-order)
      (assoc :ctx/max-speed max-speed)))

(defn create-db [ctx]
  (assoc ctx :ctx/db (db/create)))

(defn create-stage-actors [ctx]
  (doseq [[actor-fn & params] [[action-bar-create]
                                [stage-dev-menu-create]
                                [hp-mana-bar-create]
                                [windows-create [stage-info-window-create
                                                 inventory-window-create]]
                                [player-state-draw-create]
                                [player-message-actor-create]]]
    (stage/addActor (:ctx/stage ctx) (apply actor-fn ctx params)))
  ctx)

(defn create-tiled-map [ctx]
  (let [{:keys [tiled-map
                start-position]} (level-fn
                                   {:level/creature-properties (moon-tiled-map/prepare-creature-tiles
                                                                 (db/all-raw (:ctx/db ctx) :properties/creatures)
                                                                 #(textures/texture-region (:ctx/textures ctx) %))
                                    :textures (:ctx/textures ctx)})]
    (assoc ctx
           :ctx/tiled-map tiled-map
           :ctx/start-position start-position)))

(defn create-grid [ctx]
  (assoc ctx
         :ctx/grid (moon-g2d/create (map-properties/get (tiled-map/getProperties (:ctx/tiled-map ctx)) "width")
                                    (map-properties/get (tiled-map/getProperties (:ctx/tiled-map ctx)) "height")
                                    (fn [position]
                                      (atom
                                       (cell/map->R
                                        {:position position
                                         :middle (mapv (partial + 0.5) position)
                                         :movement (case (movement-property/f (:ctx/tiled-map ctx) position)
                                                      "none" :none
                                                      "air" :air
                                                      "all" :all)
                                         :entities #{}
                                         :occupied #{}}))))))

(defn create-content-grid [ctx]
  (let [width (map-properties/get (tiled-map/getProperties (:ctx/tiled-map ctx)) "width")
        height (map-properties/get (tiled-map/getProperties (:ctx/tiled-map ctx)) "height")
        cell-size 16]
    (assoc ctx :ctx/content-grid (content-grid/create width height cell-size))))

(defn create-explored-tile-corners [ctx]
  (assoc ctx
         :ctx/explored-tile-corners (atom (moon-g2d/create (map-properties/get (tiled-map/getProperties (:ctx/tiled-map ctx)) "width")
                                                            (map-properties/get (tiled-map/getProperties (:ctx/tiled-map ctx)) "height")
                                                            (constantly false)))))

(defn create-raycaster [ctx]
  (let [grid (:ctx/grid ctx)
        width (moon-g2d/width grid)
        height (moon-g2d/height grid)
        cells (for [cell (map deref (moon-g2d/cells grid))]
                [(:position cell)
                 (boolean (cell/blocks-vision? cell))])
        arr (make-array Boolean/TYPE width height)]
    (doseq [[[x y] blocked?] cells]
      (aset arr x y (boolean blocked?)))
    (assoc ctx :ctx/raycaster [arr width height])))

(defn create-spawn-player [ctx]
  (do! ctx
       [[:tx/spawn-creature {:position (mapv (partial + 0.5) (:ctx/start-position ctx))
                             :creature-property (db/build (:ctx/db ctx) :creatures/vampire)
                             :components {:entity/fsm {:fsm :fsms/player
                                                       :initial-state :player-idle}
                                          :entity/faction :good
                                          :entity/player? true
                                          :entity/free-skill-points 3
                                          :entity/clickable {:type :clickable/player}
                                          :entity/click-distance-tiles 1.5}}]])
  ctx)

(defn create-player-eid [ctx]
  (let [eid (get @(:ctx/entity-ids ctx) 1)]
    (assert (:entity/player? @eid))
    (assoc ctx :ctx/player-eid eid)))

(defn create-spawn-creatures [ctx]
  (do! ctx
       (for [[position creature-id] (spawn-positions/f (:ctx/tiled-map ctx))]
         [:tx/spawn-creature {:position (mapv (partial + 0.5) position)
                              :creature-property (db/build (:ctx/db ctx) (keyword creature-id))
                              :components {:entity/fsm {:fsm :fsms/npc
                                                        :initial-state :npc-sleeping}
                                           :entity/faction :evil}}]))
  ctx)

(defn create-dissoc-files [ctx]
  (dissoc ctx :ctx/files))

(defn stage-ctx
  [{:keys [ctx/stage] :as ctx}]
  (or (:stage/ctx stage) ctx))

(def schema
  (malli-schema/create
   [:map {:closed true}
    [:ctx/input :some]
    [:ctx/graphics :some]
    [:ctx/audio :some]
    [:ctx/batch :some]
    [:ctx/cursors :some]
    [:ctx/default-font :some]
    [:ctx/unit-scale :some]
    [:ctx/world-viewport :some]
    [:ctx/shape-drawer :some]
    [:ctx/shape-drawer-texture :some]
    [:ctx/textures :some]
    [:ctx/skin :some]
    [:ctx/stage :some]
    [:ctx/active-entities :any]
    [:ctx/delta-time :any]
    [:ctx/mouseover-eid :any]
    [:ctx/ui-mouse-position :any]
    [:ctx/world-mouse-position :any]
    [:ctx/colors :some]
    [:ctx/controls :some]
    [:ctx/controls-info :some]
    [:ctx/max-speed :some]
    [:ctx/render-z-order :some]
    [:ctx/content-grid :some]
    [:ctx/entity-ids :some]
    [:ctx/explored-tile-corners :some]
    [:ctx/grid :some]
    [:ctx/id-counter :some]
    [:ctx/potential-field-cache :some]
    [:ctx/raycaster :some]
    [:ctx/start-position :some]
    [:ctx/tiled-map :some]
    [:ctx/db :some]
    [:ctx/elapsed-time :some]
    [:ctx/paused? :some]
    [:ctx/player-eid :some]
    [:ctx/show-potential-field-colors? :any]
    [:ctx/show-cell-entities? :boolean]
    [:ctx/show-cell-occupied? :boolean]
    [:ctx/show-body-bounds? :boolean]
    [:ctx/show-tile-grid? :boolean]]))

(defn render-validate [ctx]
  (malli-schema/validate-humanize schema ctx)
  ctx)

(defn update-mouse-positions
  [{:keys [ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (let [mp (mouse-position ctx)]
    (-> ctx
        (assoc :ctx/world-mouse-position (unproject world-viewport mp))
        (assoc :ctx/ui-mouse-position (-> stage :stage/viewport (unproject mp))))))

(defn update-mouseover-eid
  [{:keys [ctx/mouseover-eid
           ctx/player-eid
           ctx/grid
           ctx/raycaster
           ctx/render-z-order
           ctx/world-mouse-position]
    :as ctx}]
  (let [position world-mouse-position
        new-eid (if (mouseover-actor ctx)
                  nil
                  (let [player @player-eid
                        hits (remove #(= (:body/z-order (:entity/body @%)) :z-order/effect)
                                     (point->entities grid position))]
                    (->> render-z-order
                         (sort-by-order/f hits #(:body/z-order (:entity/body @%)))
                         reverse
                         (filter #(raycaster/line-of-sight? raycaster player @%))
                         first)))]
    (when mouseover-eid
      (swap! mouseover-eid dissoc :entity/mouseover?))
    (when new-eid
      (swap! new-eid assoc :entity/mouseover? true))
    (assoc ctx :ctx/mouseover-eid new-eid)))

(defn check-debug-viewer
  [{:keys [ctx/input
           ctx/controls
           ctx/mouseover-eid
           ctx/skin
           ctx/stage
           ctx/grid
           ctx/world-mouse-position]
    :as ctx}]
  (when (input/button-just-pressed? input (:open-debug-button controls))
    (let [data (or (and mouseover-eid @mouseover-eid)
                   @(grid (mapv int world-mouse-position)))]
      (stage/addActor stage
                        (data-viewer-window/create
                         {:title "Data View"
                          :data data
                          :width 500
                          :height 500
                          :skin skin}))))
  ctx)

(defn set-active-entities
  [{:keys [ctx/player-eid
           ctx/content-grid]
    :as ctx}]
  (assoc ctx :ctx/active-entities
         (content-grid/active-entities content-grid @player-eid)))

(defn set-camera-position
  [{:keys [ctx/player-eid
           ctx/world-viewport]
    :as ctx}]
  (orthographic-camera/set-position! (viewport/getCamera world-viewport)
                                     (:body/position (:entity/body @player-eid)))
  ctx)

(defn clear-screen
  [{:keys [ctx/graphics] :as ctx}]
  (let [gl (graphics/getGL20 graphics)]
    (gl20/glClearColor gl 0 0 0 0)
    (gl20/glClear gl gl20/GL_COLOR_BUFFER_BIT))
  ctx)

(defn render-draw-tiled-map
  [{:keys [ctx/batch
           ctx/colors
           ctx/explored-tile-corners
           ctx/raycaster
           ctx/tiled-map
           ctx/world-viewport]
    :as ctx}]
  (draw-tiled-map/draw! batch
                     world-unit-scale
                     (viewport/getCamera world-viewport)
                     tiled-map
                     (tile-color-setter*
                      {:ray-blocked? (partial raycaster/blocked? raycaster)
                       :explored-tile-corners explored-tile-corners
                       :light-position (orthographic-camera/position (viewport/getCamera world-viewport))
                       :see-all-tiles? false
                       :explored-tile-color (:colors/explored-tile colors)
                       :visible-tile-color (:colors/visible-tile colors)
                       :invisible-tile-color (:colors/invisible-tile colors)}))
  ctx)

(def ^:private render-layers
  [#{:entity/mouseover?
     :stunned
     :player-item-on-cursor}
   #{:entity/clickable
     :entity/animation
     :entity/image
     :entity/line-render}
   #{:npc-sleeping
     :entity/temp-modifier
     :entity/string-effect}
   #{:entity/stats
     :active-skill}])

(defn- draw-tile-grid
  [{:keys [ctx/world-viewport
           ctx/show-tile-grid?]}]
  (if show-tile-grid?
    (let [[left-x _right-x bottom-y _top-y] (orthographic-camera/frustum (viewport/getCamera world-viewport))]
      [[:draw/grid
        (int left-x)
        (int bottom-y)
        (inc (int (viewport/getWorldWidth world-viewport)))
        (+ 2 (int (viewport/getWorldHeight world-viewport)))
        1
        1
        (color/toFloatBits [1 1 1 0.8])]])
    []))

(defn- draw-cell-debug
  [{:keys [ctx/colors
           ctx/grid
           ctx/world-viewport
           ctx/show-potential-field-colors?
           ctx/show-cell-entities?
           ctx/show-cell-occupied?]}]
  (apply concat
         (for [[x y] (orthographic-camera/visible-tiles (viewport/getCamera world-viewport))
               :let [cell (grid [x y])]
               :when cell
               :let [cell* @cell]]
           [(when (and show-cell-entities? (seq (:entities cell*)))
              [:draw/filled-rectangle x y 1 1 (:colors/debug-cell-entities colors)])
            (when (and show-cell-occupied? (seq (:occupied cell*)))
              [:draw/filled-rectangle x y 1 1 (:colors/debug-cell-occupied colors)])
            (when-let [faction show-potential-field-colors?]
              (let [{:keys [distance]} (faction cell*)]
                (when distance
                  (let [ratio (/ distance (factions-iterations faction))]
                    [:draw/filled-rectangle x y 1 1 ((:colors/debug-potential-field colors) ratio)]))))])))

(defn draw-entity-rectangle!
  [ctx
   entity
   color-float-bits]
  (draw! ctx
         (let [{:keys [body/position
                       body/width
                       body/height]} (:entity/body entity)
               [x y] [(- (position 0) (/ width  2))
                      (- (position 1) (/ height 2))]]
           [[:draw/rectangle x y width height color-float-bits]])))

(defn- draw-entities!
  [{:keys [ctx/active-entities
           ctx/colors
           ctx/player-eid
           ctx/raycaster
           ctx/render-z-order
           ctx/show-body-bounds?]
    :as ctx}]
  (let [entities (map deref active-entities)
        player @player-eid
        should-draw? (fn [entity z-order]
                       (or (= z-order :z-order/effect)
                           (raycaster/line-of-sight? raycaster player entity)))]
    (doseq [[z-order entities] (sort-by-order/f (group-by (comp :body/z-order :entity/body) entities)
                                                first
                                                render-z-order)
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (try
        (do
          (when show-body-bounds?
            (draw-entity-rectangle! ctx
                                    entity
                                    (if (:body/collides? (:entity/body entity))
                                      (:colors/debug-body-outline-collides colors)
                                      (:colors/debug-body-outline colors))))
          (doseq [[k v] entity
                  :when (get render-layer k)]
            (draw! ctx (draw-component ctx entity k v))))
        (catch Throwable t
          (draw-entity-rectangle! ctx
                                  entity
                                  (:colors/debug-body-outline-render-error colors))
          (throwable/pretty-pst t))))))

(defn- highlight-mouseover-tile
  [{:keys [ctx/colors
           ctx/grid
           ctx/world-mouse-position]}]
  (let [[x y] (mapv int world-mouse-position)
        cell (grid [x y])]
    (when (and cell (#{:air :none} (:movement @cell)))
      [[:draw/rectangle x y 1 1
        (case (:movement @cell)
          :air (:colors/mouseover-tile-air colors)
          :none (:colors/mouseover-tile-none colors))]])))

(defn draw-on-world-viewport
  [{:keys [ctx/batch
           ctx/shape-drawer
           ctx/unit-scale
           ctx/world-viewport]
    :as ctx}]
  (batch/setColor batch 1 1 1 1)
  (batch/setProjectionMatrix batch (orthographic-camera/combined (viewport/getCamera world-viewport)))
  (batch/begin batch)
  (let [old-line-width (shape-drawer/getDefaultLineWidth shape-drawer)]
    (shape-drawer/setDefaultLineWidth shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [draw-fn [draw-tile-grid
                     draw-cell-debug
                     draw-entities!
                     highlight-mouseover-tile]]
      (draw! ctx (draw-fn ctx)))
    (reset! unit-scale 1)
    (shape-drawer/setDefaultLineWidth shape-drawer old-line-width))
  (batch/end batch)
  ctx)

(defn- make-interaction-state
  [{:keys [ctx/mouseover-eid
           ctx/stage
           ctx/player-eid
           ctx/world-mouse-position]
    :as ctx}]
  (let [mouseover-actor* (mouseover-actor ctx)]
    (cond
      mouseover-actor*
      [:interaction-state/mouseover-actor (mouseover-actor-info mouseover-actor*)]

      (and mouseover-eid
           (:entity/clickable @mouseover-eid))
      [:interaction-state/clickable-mouseover-eid
       {:clicked-eid mouseover-eid
        :in-click-range? (< (v2/distance (:body/position (:entity/body @player-eid))
                                        (:body/position (:entity/body @mouseover-eid)))
                            (:entity/click-distance-tiles @player-eid))}]

      :else
      (if-let [skill-id (-> stage
                            :stage/root
                            (#(group/findActor % "moon.ui.action-bar"))
                            action-bar/selected-skill)]
        (let [entity @player-eid
              skill (skill-id (:entity/skills entity))
              effect-ctx (player-effect-ctx mouseover-eid world-mouse-position player-eid)
              state (skill-usable-state skill entity effect-ctx)]
          (if (= state :usable)
            [:interaction-state.skill/usable [skill effect-ctx]]
            [:interaction-state.skill/not-usable state]))
        [:interaction-state/no-skill-selected]))))

(defn assoc-interaction-state [ctx]
  (assoc ctx :ctx/interaction-state (make-interaction-state ctx)))

(def k->cursor
  {:player-item-on-cursor :cursors/hand-grab
   :player-dead :cursors/black-x
   :active-skill :cursors/sandclock
   :stunned :cursors/denied
   :player-moving :cursors/walking
   :player-idle (fn
                  [eid {:keys [ctx/interaction-state]}]
                  (let [[k params] interaction-state]
                    (case k
                      :interaction-state/mouseover-actor
                      (let [[actor-type params] params
                            inventory-cell-with-item? (and (= actor-type :mouseover-actor/inventory-cell)
                                                           (let [inventory-slot params]
                                                             (get-in (:entity/inventory @eid) inventory-slot)))]
                        (cond
                         inventory-cell-with-item?
                         :cursors/hand-before-grab

                         (= actor-type :mouseover-actor/window-title-bar)
                         :cursors/move-window

                         (= actor-type :mouseover-actor/button)
                         :cursors/over-button

                         (= actor-type :mouseover-actor/unspecified)
                         :cursors/default

                         :else
                         :cursors/default))

                      :interaction-state/clickable-mouseover-eid
                      (let [{:keys [clicked-eid
                                    in-click-range?]} params]
                        (case (:type (:entity/clickable @clicked-eid))
                          :clickable/item (if in-click-range?
                                            :cursors/hand-before-grab
                                            :cursors/hand-before-grab-gray)
                          :clickable/player :cursors/bag))

                      :interaction-state.skill/usable
                      :cursors/use-skill

                      :interaction-state.skill/not-usable
                      :cursors/skill-not-usable

                      :interaction-state/no-skill-selected
                      :cursors/no-skill-selected)))})

(defn set-cursor
  [{:keys [ctx/graphics
           ctx/cursors
           ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-fn (k->cursor state-k)
        cursor-key (if (keyword? cursor-fn)
                     cursor-fn
                     (cursor-fn eid ctx))]
    (assert (contains? cursors cursor-key))
    (graphics/setCursor graphics (get cursors cursor-key)))
  ctx)

(defn handle-player-input
  [{:keys [ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (if-let [input-fn (k->handle-input state-k)]
              (input-fn eid ctx)
              nil)]
    (do! ctx txs))
  ctx)

(defn dissoc-interaction-state [ctx]
  (dissoc ctx :ctx/interaction-state))

(defn assoc-paused
  [{:keys [ctx/input
           ctx/controls
           ctx/player-eid]
    :as ctx}]
  (assoc ctx :ctx/paused?
         (or #_error
             (and pausing?
                  (state->pause-game? (:state (:entity/fsm @player-eid)))
                  (not (or (input/key-just-pressed? input (:unpause-once controls))
                           (input/key-pressed? input (:unpause-continously controls))))))))

(defn- update-time
  [{:keys [ctx/graphics]
    :as ctx}]
  (let [delta-ms (min (graphics/getDeltaTime graphics) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))

(defn- update-potential-fields
  [{:keys [ctx/active-entities
           ctx/grid
           ctx/potential-field-cache]
    :as ctx}]
  (doseq [[faction max-iterations] factions-iterations]
    (grid/update! grid
                  potential-field-cache
                  faction
                  active-entities
                  max-iterations))
  ctx)

(defn- tick-entities
  [{:keys [ctx/active-entities
           ctx/skin
           ctx/stage]
    :as ctx}]
  (try
    (do! ctx
         (mapcat (fn [eid]
                   (mapcat (fn [component]
                             (try (tick-component ctx eid component)
                                  (catch Throwable t
                                    (throw (ex-info "Error at `entity/tick`:" {:eid eid} t)))))
                           @eid))
                 active-entities))
    (catch Throwable t
      (throwable/pretty-pst t)
      (stage/addActor stage
                        (error-window/create
                         {:skin skin
                          :throwable t}))))
  ctx)

(defn when-not-paused [ctx]
  (if (:ctx/paused? ctx)
    ctx
    (reduce (fn [ctx step] (step ctx))
            ctx
            [update-time
             update-potential-fields
             tick-entities])))

(def k->destroy
  {
   :entity/destroy-audiovisual
   (fn
     [audiovisuals-id eid]
     [[:tx/audiovisual (:body/position (:entity/body @eid)) audiovisuals-id]])
   })

(defn remove-destroyed-entities [ctx]
  (do! ctx
       (mapcat
        (fn [eid]
          (cons
           [:tx/unregister-eid eid]
           (mapcat (fn [[k v]]
                     (if-let [destroy-fn (k->destroy k)]
                       (destroy-fn v eid)
                       nil))
                   @eid)))
        (filter (comp :entity/destroyed? deref)
                (vals @(:ctx/entity-ids ctx)))))
  ctx)

(def zoom-speed 0.025)

(defn window-camera-controls
  [{:keys [ctx/input
           ctx/controls
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (when (input/key-pressed? input (:zoom-in controls))
    (orthographic-camera/inc-zoom! (viewport/getCamera world-viewport) zoom-speed))

  (when (input/key-pressed? input (:zoom-out controls))
    (orthographic-camera/inc-zoom! (viewport/getCamera world-viewport) (- zoom-speed)))

  (when (input/key-just-pressed? input (:close-windows-key controls))
    (->> (group/findActor (:stage/root stage) "moon.ui.windows")
         group/getChildren
         (run! #(actor/setVisible % false))))

  (when (input/key-just-pressed? input (:toggle-inventory controls))
    (let [inventory (group/findActor (:stage/root stage) "moon.ui.windows.inventory")]
      (actor/setVisible inventory (not (actor/isVisible inventory)))))

  (when (input/key-just-pressed? input (:toggle-entity-info controls))
    (let [entity-info (group/findActor (:stage/root stage) "moon.ui.windows.entity-info")]
      (actor/setVisible entity-info (not (actor/isVisible entity-info)))))
  ctx)

(defn update-draw-stage
  [{:keys [ctx/stage] :as ctx}]
  (set-ctx/f stage ctx)
  (stage/act stage)
  (stage/draw stage)
  (:stage/ctx stage))

(def state (atom nil))

(defn create [application]
  (-> application
      create-bootstrap
      create-batch
      create-audio
      create-shape-drawer-texture
      create-shape-drawer
      create-skin
      create-stage
      create-init-tooltip
      create-cursors
      create-textures
      create-world-viewport
      create-default-font
      create-context
      create-game-config
      create-db
      create-stage-actors
      create-tiled-map
      create-grid
      create-content-grid
      create-explored-tile-corners
      create-raycaster
      create-spawn-player
      create-player-eid
      create-spawn-creatures
      create-dissoc-files))

(defn dispose
  [{:keys [ctx/audio
           ctx/batch
           ctx/cursors
           ctx/default-font
           ctx/shape-drawer-texture
           ctx/skin
           ctx/textures
           ctx/tiled-map]}]
  (audio/dispose! audio)
  (disposable/dispose! batch)
  (run! disposable/dispose! (vals cursors))
  (disposable/dispose! default-font)
  (disposable/dispose! shape-drawer-texture)
  (disposable/dispose! skin)
  (run! disposable/dispose! (vals textures))
  (disposable/dispose! tiled-map))

(defn render [ctx]
  (-> ctx
      stage-ctx
      render-validate
      update-mouse-positions
      update-mouseover-eid
      check-debug-viewer
      set-active-entities
      set-camera-position
      clear-screen
      render-draw-tiled-map
      draw-on-world-viewport
      assoc-interaction-state
      set-cursor
      handle-player-input
      dissoc-interaction-state
      assoc-paused
      when-not-paused
      remove-destroyed-entities
      window-camera-controls
      update-draw-stage
      render-validate))

(defn resize
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (viewport/update (:stage/viewport stage) width height true)
  (viewport/update world-viewport width height false))

(defn -main []
  (application/create
   {:create! (fn [app]
               (reset! state (create app)))
    :dispose! (fn []
                (dispose @state))
    :render! (fn []
               (swap! state render))
    :resize! (fn [width height]
               (resize @state width height))
    :pause! (fn [])
    :resume! (fn [])}
   {:config/set-title "Moon"
    :config/set-windowed-mode {:width 1440
                               :height 900}
    :config/set-foreground-fps 60}))
