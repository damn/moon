(ns clojure.tx
  (:require [clojure.ui.action-bar.add-skill :as add-skill-ui]
            [clojure.scene2d.actor.add-listener]
            [clojure.stats.add-mods :as add-mods]
            [clojure.moon.after-create-component :refer [after-create-component]]
            [clojure.gdx.utils.align :as align]
            [clojure.v2.angle-from-vector :as angle-from-vector]
            [clojure.scene2d.actor.set-position! :refer [set-position!]]
            [clojure.build :refer [build]]
            [clojure.can-pickup-item :as can-pickup-item]
            [clojure.moon.create-component :refer [create-component]]
            [clojure.create-entity-state :as create-entity-state]
            [clojure.scene2d.group :as group]
            [clojure.handle :as handle]
            [clojure.increment :as increment]
            [clojure.inventory-window-remove-item :as remove-item-ui]
            [clojure.inventory-window-set-item :as set-item-ui]
            [clojure.is-applicable :as applicable?]
            [clojure.is-applies-modifiers :as applies-modifiers?]
            [clojure.is-stackable :as stackable?]
            [clojure.is-valid-slot :as valid-slot?]
            [clojure.info :refer [info-text]]
            [clojure.item :as item]
            [clojure.item-is-valid :as valid?]
            [clojure.k-state-enter :refer [k->state-enter]]
            [clojure.k-state-exit :refer [k->state-exit]]
            [clojure.moon-textures :as textures]
            [clojure.play :as play]
            [clojure.records-entity :as entity]
            [clojure.register-eid :as register-eid]
            [clojure.scene2d.actor.remove-actor]
            [clojure.content-grid.remove-entity :as remove-entity]
            [clojure.remove-from-occupied-cells :refer [remove-from-occupied-cells!]]
            [clojure.remove-from-touched-cells :refer [remove-from-touched-cells!]]
            [clojure.stats.remove-mods :as remove-mods]
            [clojure.safe-merge :refer [safe-merge]]
            [clojure.set-occupied-cells :refer [set-occupied-cells!]]
            [clojure.set-touched-cells :refer [set-touched-cells!]]
            [clojure.scene2d.actor.set-user-object]
            [clojure.scene2d.actor.set-visible]
            [clojure.scene2d.actor.set-name]
            [clojure.stage :as stage]
            [clojure.timer-create :refer [create-timer]]
            [clojure.ui-label :as label]
            [clojure.ui-text-button :as text-button]
            [clojure.ui-window :as window]
            [clojure.content-grid.update-entity :as update-entity]
            [clojure.utils-change-listener :as change-listener]
            [clojure.scene2d.actor.visible]
            [clojure.window :as gdx-window]
            [reduce-fsm :as fsm]))

(def f
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
             (update :counter increment/f duration))
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
                                                  (build db audiovisual)
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
     (mapcat #(handle/f % effect-ctx ctx)
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
     (update-entity/f! content-grid eid)
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
         (#(group/find-actor % "player-message"))
         (clojure.scene2d.actor.set-user-object/f (atom {:text message :counter 0})))
     nil)

   :tx/show-modal
   (fn [{:keys [ctx/skin ctx/stage] :as _ctx}
        {:keys [title text button-text on-click]}]
     (assert (not (group/find-actor (:stage/root stage) "moon.ui.modal-window")))
     (stage/add-actor! stage
                       (doto (window/create
                              {:title title
                               :skin skin
                               :table/rows [[{:actor (label/create {:text text :skin skin})}]
                                            [{:actor (doto (text-button/create {:text button-text :skin skin})
                                                        (clojure.scene2d.actor.add-listener/f
                                                         (change-listener/create
                                                          (fn [_event _actor]
                                                            (clojure.scene2d.actor.remove-actor/f
                                                             (group/find-actor (:stage/root stage)
                                                                               "moon.ui.modal-window"))
                                                            (on-click)))))}]]})
                         (gdx-window/set-modal! true)
                         (clojure.scene2d.actor.set-name/f "moon.ui.modal-window")
                         (set-position! [(/ (:viewport/world-width (:stage/viewport stage)) 2)
                                         (* (:viewport/world-height (:stage/viewport stage)) (/ 3 4))]
                                        align/center)))
     nil)

   :tx/sound
   (fn [{:keys [ctx/audio]} sound-name]
     (let [sounds audio]
       (assert (contains? sounds sound-name) (str sound-name))
       (play/f (get sounds sound-name)))
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
                      :rotation-angle (angle-from-vector/f direction)}
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
     (let [inventory (group/find-actor (:stage/root stage) "moon.ui.windows.inventory")]
       (clojure.scene2d.actor.set-visible/f inventory (not (clojure.scene2d.actor.visible/f inventory)))
       nil))

   :tx/ui-remove-item
   (fn [{:keys [ctx/stage]} _eid cell]
     (-> stage
         :stage/root
         (#(group/find-actor % "moon.ui.windows.inventory"))
         (remove-item-ui/f cell))
     nil)

   :tx/ui-set-item
   (fn [{:keys [ctx/skin ctx/stage ctx/textures] :as ctx} _eid cell item]
     (-> stage
         :stage/root
         (#(group/find-actor % "moon.ui.windows.inventory"))
         (set-item-ui/f cell
                        {:texture-region (textures/texture-region textures (:entity/image item))
                         :tooltip-text (item/info-text item ctx)}
                        skin))
     nil)

   :tx/ui-update-skill
   (fn [{:keys [ctx/skin ctx/stage ctx/textures] :as ctx} _eid skill]
     (-> stage
         :stage/root
         (#(group/find-actor % "moon.ui.action-bar"))
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
     (remove-entity/f! content-grid eid)
     (remove-from-touched-cells! grid eid)
     (when (:body/collides? (:entity/body @eid))
       (remove-from-occupied-cells! grid eid))
     nil)

   :tx/update
   (fn [_ctx eid & params]
     (apply swap! eid update params)
     nil)})
