(ns moon.game
  (:require [clojure.edn :as edn]
            [clojure.int-between :refer [rand-int-between]]
            [clojure.inventory-window-remove-item :as remove-item-ui]
            [clojure.inventory-window-set-item :as set-item-ui]
            [clojure.inventory.can-pickup-item :as can-pickup-item]
            [clojure.is-applicable :as applicable?]
            [clojure.is-applies-modifiers :as applies-modifiers?]
            [clojure.is-nearly-equal :as nearly-equal?]
            [clojure.is-stackable :as stackable?]
            [clojure.is-valid-slot :as valid-slot?]
            [clojure.item-is-valid :as valid?]
            [clojure.item-place-position :refer [item-place-position]]
            [clojure.java.io :as io]
            [clojure.levels.uf-caves :as uf-caves]
            [clojure.malli-form-register-methods]
            [clojure.malli.schema :as malli-schema]
            [clojure.math :as math]
            [clojure.menus.help :refer [controls-info]]
            [clojure.menus.v :as menus]
            [clojure.minimum-size :refer [minimum-size]]
            [clojure.moon-faction :as faction]
            [clojure.moon-target-all :as target-all]
            [clojure.moon.after-create-component :refer [after-create-component]]
            [clojure.moon.choose-skill :as choose-skill]
            [clojure.moon.create-component :refer [create-component]]
            [clojure.moon.create-entity-state :as create-entity-state]
            [clojure.moon.draw :refer [draw!]]
            [clojure.moon.effect-render :as effect-render]
            [clojure.moon.entity-state-draw-ui-view :as entity-state-draw-ui-view]
            [clojure.moon.handle-clicked-inventory-cell :as handle-clicked-inventory-cell]
            [clojure.moon.k-handle-input.player-idle :as player-idle]
            [clojure.moon.k-handle-input.player-item-on-cursor :as player-item-on-cursor-input]
            [clojure.moon.k-handle-input.player-moving :as player-moving]
            [clojure.moon.npc-effect-ctx :as create-effect-ctx]
            [clojure.moon.schema :refer [schema]]
            [clojure.moon.state-enter :refer [k->state-enter]]
            [clojure.moon.state-exit :refer [k->state-exit]]
            [clojure.moon.world-unit-scale :refer [world-unit-scale]]
            [clojure.moon.z-orders :refer [z-orders]]
            [clojure.mouse-position :refer [mouse-position]]
            [clojure.mouseover-actor :refer [mouseover-actor]]
            [clojure.move :as move]
            [clojure.movement-property :as movement-property]
            [clojure.nearest-enemy-distance :refer [nearest-enemy-distance]]
            [clojure.orthographic-camera-position :as get-position]
            [clojure.orthographic-camera-set-position :as camera-set-position]
            [clojure.orthographic-camera.visible-tiles :refer [visible-tiles]]
            [clojure.overlaps :refer [overlaps?]]
            [clojure.point-to-entities :refer [point->entities]]
            [clojure.projectile-start-point :as projectile-start-point]
            [clojure.ratio :as timer-ratio]
            [clojure.readable :as readable]
            [clojure.records-entity :as entity]
            [clojure.register-eid :as register-eid]
            [clojure.remove-from-occupied-cells :refer [remove-from-occupied-cells!]]
            [clojure.remove-from-touched-cells :refer [remove-from-touched-cells!]]
            [clojure.remove-newlines :refer [remove-newlines]]
            [clojure.safe-merge :refer [safe-merge]]
            [clojure.scene2d-stage :as scene2d-stage]
            [clojure.scene2d.actor.mouseover-info :refer [mouseover-actor-info]]
            [clojure.set-ctx :as set-ctx]
            [clojure.set-occupied-cells :refer [set-occupied-cells!]]
            [clojure.set-touched-cells :refer [set-touched-cells!]]
            [clojure.sort-by-k-order :refer [sort-by-k-order]]
            [clojure.sort-by-order :as sort-by-order]
            [clojure.spawn-positions :as spawn-positions]
            [clojure.stats.add-mods :as add-mods]
            [clojure.stats.calc-damage :as calc-damage]
            [clojure.stats.effective-armor-save :as effective-armor-save]
            [clojure.stats.melee-damage :as melee-damage]
            [clojure.stats.remove-mods :as remove-mods]
            [clojure.stopped :refer [stopped?]]
            [clojure.string :as str]
            [clojure.table-set-opts :as table-set-opts]
            [clojure.throwable :as throwable]
            [clojure.tiled-map.creature-tiles :as creature-tiles]
            [clojure.timer-create :refer [create-timer]]
            [clojure.try-move-solid-body :as try-move-solid-body]
            [clojure.txs-fn-map.actions :refer [actions!]]
            [clojure.ui-info-window :as info-window]
            [clojure.ui.action-bar.add-skill :as add-skill-ui]
            [clojure.ui.action-bar.selected-skill :as selected-skill]
            [clojure.ui.data-viewer-window :as data-viewer-window]
            [clojure.ui.dev-menu :as dev-menu]
            [clojure.ui.error-window :as error-window]
            [clojure.ui.inventory-window :refer [inventory-window-build]]
            [clojure.unproject :as unproject]
            [clojure.update-effect-ctx :as update-effect-ctx]
            [clojure.update-labels :as update-labels]
            [clojure.usable-state :as usable-state]
            [clojure.val-max.ratio :as ratio]
            [com.badlogic.gdx.application :as application]
            [com.badlogic.gdx.graphics :as graphics]
            [com.badlogic.gdx.graphics.color :as color]
            [com.badlogic.gdx.graphics.colors :as colors]
            [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]
            [com.badlogic.gdx.graphics.g2d.bitmap-font$bitmap-font-data :as bitmap-font-data]
            [com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.graphics.gl20 :as gl20]
            [com.badlogic.gdx.graphics.glutils.pixmap-texture-data :as pixmap-texture-data]
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [com.badlogic.gdx.graphics.pixmap :as pixmap]
            [com.badlogic.gdx.graphics.pixmap$format :as pixmap-format]
            [com.badlogic.gdx.graphics.texture :as texture]
            [com.badlogic.gdx.graphics.texture$texture-filter :as texture-filter]
            [com.badlogic.gdx.input :as input]
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
            [gdl.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
            [gdl.graphics.g2d.freetype.font-generator :as free-type-font-generator]
            [gdl.input.buttons :as input-buttons]
            [gdl.input.keys :as input-keys]
            [gdl.maps.map-properties :as map-properties]
            [gdx.files :as files]
            [gdx.graphics.g2d.batch.draw-tiled-map :as draw-tiled-map]
            [gdx.graphics.orthographic-camera :as gdx-orthographic-camera]
            [gdx.utils.disposable :as disposable]
            [moon.audio :as audio]
            [moon.body :as body]
            [moon.cell :as cell]
            [moon.content-grid :as content-grid]
            [moon.db :as db]
            [moon.g2d :as moon-g2d]
            [moon.grid :as grid]
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
           (for [target (target-all/affected-targets active-entities raycaster source*)]
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
          (< (rand) (effective-armor-save/f (:entity/stats source*)
                                            (:entity/stats target*))))
     [[:tx/add-text-effect target "[WHITE]ARMOR" 0.3]]

     :else
     (let [min-max (if (:entity/stats source*)  ; projectiles dont have ....
                     (:damage/min-max (calc-damage/f (:entity/stats source*)
                                                     (:entity/stats target*)
                                                     damage))
                     (:damage/min-max damage))
           dmg-amount (rand-int-between min-max)
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
  (handle-effect [:effects.target/damage (melee-damage/f @source)] effect-ctx ctx))

(defmethod handle-effect :effects.target/spiderweb
  [_ {:keys [effect/target]} {:keys [ctx/elapsed-time]}]
  ; TODO stacking? (if already has k ?) or reset counter ? (see string-effect too)
  (when-not (:entity/temp-modifier @target)
    [[:tx/assoc target :entity/temp-modifier {:modifiers spiderweb-modifiers
                                              :counter (create-timer elapsed-time spiderweb-duration)}]
     [:tx/update target :entity/stats add-mods/f spiderweb-modifiers]]))

(defmethod handle-effect :effects.target/stun
  [[_ duration] {:keys [effect/target]} _ctx]
  [[:tx/event target :stun duration]])

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
         (sort-by-k-order k-order)
         (keep (fn [{k 0 v 1 :as component}]
                 (str (try (component-info component)
                           (catch Throwable _t
                             (str "*info-error* " k)))
                      (when (map? v)
                        (str "\n" (info-text v ctx))))))
         (str/join "\n")
         remove-newlines)))

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
             (filter #(applicable?/f % effect-ctx) effects)))

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
                   new-state-obj [new-state-k (create-entity-state/f [new-state-k nil] eid ctx)]]
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
                   new-state-obj [new-state-k (create-entity-state/f [new-state-k params] eid ctx)]]
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
     (assert (valid?/f item))
     (let [[cell cell-item] (can-pickup-item/f? (:entity/inventory @eid) item)]
       (assert cell)
       (assert (or (stackable?/f item cell-item)
                   (nil? cell-item)))
       (if (stackable?/f item cell-item)
         (do #_(tx/stack-item ctx eid cell item))
         [[:tx/set-item eid cell item]])))

   :tx/remove-item
   (fn [ctx eid cell]
     (let [entity @eid
           item (get-in (:entity/inventory entity) cell)]
       (assert item)
       [[:tx/assoc-in eid (cons :entity/inventory cell) nil]
        (when (applies-modifiers?/f cell)
          [:tx/update eid :entity/stats remove-mods/f (:stats/modifiers item)])
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
                    (valid-slot?/f cell item)))
       [[:tx/assoc-in eid (cons :entity/inventory cell) item]
        (when (applies-modifiers?/f cell)
          [:tx/update eid :entity/stats add-mods/f (:stats/modifiers item)])
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
           entity (merge (entity/map->R {}) entity)
           eid (atom entity)]
       (register-eid/do! ctx eid)
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
         (remove-item-ui/f cell))
     nil)

   :tx/ui-set-item
   (fn [{:keys [ctx/skin ctx/stage ctx/textures] :as ctx} _eid cell item]
     (-> stage
         :stage/root
         (#(group/findActor % "moon.ui.windows.inventory"))
         (set-item-ui/f cell
                        {:texture-region (textures/texture-region textures (:entity/image item))
                         :tooltip-text (item/info-text item)}
                        skin))
     nil)

   :tx/ui-update-skill
   (fn [{:keys [ctx/skin ctx/stage ctx/textures] :as ctx} _eid skill]
     (-> stage
         :stage/root
         (#(group/findActor % "moon.ui.action-bar"))
         (add-skill-ui/f {:skill-id (:property/id skill)
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
  (let [ratio (ratio/f (stats/get-hitpoints (:entity/stats entity)))]
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
            (mapcat #(effect-render/f % effect-ctx ctx)
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
                                                        :image/bounds [0 0 (* rahmenw (ratio/f minmaxval)) rahmenh]})
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
      :on-click-cell handle-clicked-inventory-cell/f
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
   {:menus menus/v
    :update-labels (for [item update-labels/v]
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

(defn player-state-draw-create [_ctx]
  (actor/new
   (fn [_actor _delta])
   (fn [this _batch _parent-alpha]
     (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (actor/getStage this))
           entity @player-eid
           state-k (:state (:entity/fsm entity))]
       (draw! ctx (entity-state-draw-ui-view/f [state-k (state-k entity)] player-eid ctx))))))

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

(def k->handle-input
  {:player-idle player-idle/f
   :player-moving player-moving/f
   :player-item-on-cursor player-item-on-cursor-input/f})

(defn player-effect-ctx [mouseover-eid world-mouse-position player-eid]
  (let [target-position (or (and mouseover-eid
                                 (:body/position (:entity/body @mouseover-eid)))
                            world-mouse-position)]
    {:effect/source player-eid
     :effect/target mouseover-eid
     :effect/target-position target-position
     :effect/target-direction (v2/direction (:body/position (:entity/body @player-eid))
                                         target-position)}))

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
        [:tx/update eid :entity/stats remove-mods/f modifiers]]))

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
                                           (overlaps? (:entity/body entity)
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
     (let [effect-ctx (update-effect-ctx/f raycaster effect-ctx)]
       (cond
        (not (seq (filter #(applicable?/f % effect-ctx)
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
       (when-let [distance (nearest-enemy-distance grid entity)]
         (when (<= distance (stats/get-value (:entity/stats entity) :stats/aggro-range))
           [[:tx/event eid :alert]]))))

   :npc-idle
   (fn [_ eid ctx]
     (let [effect-ctx (create-effect-ctx/f ctx eid)]
       (if-let [skill (choose-skill/f ctx @eid effect-ctx)]
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
                 (nearly-equal?/f 1 (v2/length direction)))
             (str "cannot understand direction: " (pr-str direction)))
     (when-not (or (zero? (v2/length direction))
                   (nil? speed)
                   (zero? speed))
       (let [movement (assoc movement :delta-time delta-time)
             body (:entity/body @eid)]
         (when-let [body (if (:body/collides? body)
                            (try-move-solid-body/f grid body (:entity/id @eid) movement)
                            (update body :body/position move/f movement))]
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
  {:ctx/audio    (application/getAudio    application)
   :ctx/files    (application/getFiles    application)
   :ctx/graphics (application/getGraphics application)
   :ctx/input    (application/getInput    application)
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
   :ctx/show-body-bounds? false})

(defn create-batch [ctx]
  (assoc ctx :ctx/batch (sprite-batch/new)))

(defn create-audio [ctx]
  (assoc ctx
         :ctx/audio (audio/create (:ctx/audio ctx) (:ctx/files ctx))))

(defn create-shape-drawer-texture [ctx]
  (let [pixmap (doto (pixmap/new 1 1 pixmap-format/RGBA8888)
                 (pixmap/setColor 1 1 1 1)
                 (pixmap/drawPixel 0 0))
        texture (texture/new (pixmap-texture-data/new pixmap
                                                      (pixmap/getFormat pixmap)
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
  (let [stage* (scene2d-stage/create (fit-viewport/new 1440 900) batch)]
    (input/setInputProcessor input stage*)
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
                                                      (orthographic-camera/setToOrtho false world-width world-height))))))

(defn create-default-font [ctx]
  (assoc ctx
         :ctx/default-font (let [{:keys [path
                                         size
                                         quality-scaling
                                         use-integer-positions?]} {:path "fonts/films.EXL_____.ttf"
                                                                   :size 16
                                                                   :quality-scaling 2
                                                                   :use-integer-positions? false}
                                 generator (free-type-font-generator/new (files/internal (:ctx/files ctx) path))
                                 parameter {
                                            :set-size (* size quality-scaling)
                                            :set-min-filter texture-filter/Linear
                                            :set-mag-filter texture-filter/Linear
                                            }
                                 font (free-type-font-generator/generate-font generator parameter)
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
                                   {:level/creature-properties (creature-tiles/prepare
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

(defn render-validate [ctx]
  (malli-schema/validate-humanize schema ctx)
  ctx)

(defn update-mouse-positions
  [{:keys [ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (let [mp (mouse-position ctx)]
    (-> ctx
        (assoc :ctx/world-mouse-position (unproject/f world-viewport mp))
        (assoc :ctx/ui-mouse-position (-> stage :stage/viewport (unproject/f mp))))))

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
  (when (input/isButtonJustPressed input (input-buttons/key-to-value (:open-debug-button controls)))
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
  (camera-set-position/set-position! (viewport/getCamera world-viewport)
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
                       :light-position (get-position/f (viewport/getCamera world-viewport))
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

(defn- draw-cell-debug
  [{:keys [ctx/colors
           ctx/grid
           ctx/world-viewport
           ctx/show-potential-field-colors?
           ctx/show-cell-entities?
           ctx/show-cell-occupied?]}]
  (apply concat
         (for [[x y] (visible-tiles (viewport/getCamera world-viewport))
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
    (doseq [draw-fn [draw-cell-debug
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
                            selected-skill/f)]
        (let [entity @player-eid
              skill (skill-id (:entity/skills entity))
              effect-ctx (player-effect-ctx mouseover-eid world-mouse-position player-eid)
              state (usable-state/f skill entity effect-ctx)]
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
                  (not (or (input/isKeyJustPressed input (input-keys/key-to-value (:unpause-once controls)))
                           (input/isKeyPressed input (input-keys/key-to-value (:unpause-continously controls)))))))))

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
  (when (input/isKeyPressed input (input-keys/key-to-value (:zoom-in controls)))
    (gdx-orthographic-camera/inc-zoom! (viewport/getCamera world-viewport) zoom-speed))

  (when (input/isKeyPressed input (input-keys/key-to-value (:zoom-out controls)))
    (gdx-orthographic-camera/inc-zoom! (viewport/getCamera world-viewport) (- zoom-speed)))

  (when (input/isKeyJustPressed input (input-keys/key-to-value (:close-windows-key controls)))
    (->> (group/findActor (:stage/root stage) "moon.ui.windows")
         group/getChildren
         (run! #(actor/setVisible % false))))

  (when (input/isKeyJustPressed input (input-keys/key-to-value (:toggle-inventory controls)))
    (let [inventory (group/findActor (:stage/root stage) "moon.ui.windows.inventory")]
      (actor/setVisible inventory (not (actor/isVisible inventory)))))

  (when (input/isKeyJustPressed input (input-keys/key-to-value (:toggle-entity-info controls)))
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
  (lwjgl3-application/create
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
