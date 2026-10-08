(ns entity.spawn
  (:require [animation.create :as animation.create]
            [game.entity :refer [set-item]]
            [info-text :refer [info-text]]
            [moon.g2d :as moon-g2d]
            [moon.inventory :as inventory]
            [moon.item :as item]
            [moon.stats :as stats]
            [moon.textures :as textures]
            [moon.timer :as timer]
            [moon.world :as world]
            [reduce-fsm :as fsm]
            [world.register-eid :refer [register-eid!]])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor Group Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Button ButtonGroup Image ImageButton Skin TextTooltip)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable TextureRegionDrawable)))

(defn create-component
  [elapsed-time k v]
  (case k
    :entity/animation
    (animation.create/create v)

    :entity/delete-after-duration
    (timer/create elapsed-time v)

    :entity/projectile-collision
    (assoc v :already-hit-bodies #{})

    :entity/stats
    (-> v
        (update :stats/mana (fn [v] [v v]))
        (update :stats/hp   (fn [v] [v v])))

    v))

(defmulti create-entity-state
  (fn [[k _v] _eid _elapsed-time]
    k))

(defmethod create-entity-state :default
  [[_k v] _eid _elapsed-time]
  v)

(defmethod create-entity-state :active-skill
  [[_k [skill effect-ctx]] eid elapsed-time]
  {:skill skill
   :effect-ctx effect-ctx
   :counter (->> skill
                 :skill/action-time
                 (stats/apply-action-speed-modifier (:entity/stats @eid) skill)
                 (timer/create elapsed-time))})

(defmethod create-entity-state :stunned
  [[_k duration] _eid elapsed-time]
  {:counter (timer/create elapsed-time duration)})

(defmethod create-entity-state :player-moving
  [[_k movement-vector] _eid _elapsed-time]
  {:movement-vector movement-vector})

(defmethod create-entity-state :npc-moving
  [[_k movement-vector] eid elapsed-time]
  {:movement-vector movement-vector
   :timer (timer/create elapsed-time
                        (* (stats/get-value (:entity/stats @eid) :stats/reaction-time)
                           0.016))})

(defmethod create-entity-state :player-item-on-cursor
  [[_k item] _eid _elapsed-time]
  {:item item})

(def ^:private fsms
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

(defn- action-bar-get-data
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (.findActor ^Group action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (.getUserObject ^Actor group)}))

(defn- action-bar-add-skill!
  [action-bar
   {:keys [skill-id
           texture-region
           tooltip-text]}
   skin]
  (let [scale 2
        {:keys [horizontal-group button-group]} (action-bar-get-data action-bar)
        button (doto (ImageButton.
                      (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                        (.setMinSize (* scale (.getRegionWidth ^TextureRegion texture-region))
                                     (* scale (.getRegionHeight ^TextureRegion texture-region)))))
                 (.addListener (TextTooltip. ^String tooltip-text ^Skin skin))
                 (.setUserObject skill-id))]
    (.addActor ^Group horizontal-group ^Actor button)
    (.add ^ButtonGroup button-group ^Button button)
    nil))

(defn- inventory-window-get-cell [inventory-window cell]
  (->> (.getChildren ^Group (.findActor ^Group inventory-window "inventory-cell-table"))
       (filter #(= (.getUserObject ^Actor %) cell))
       first))

(defn- inventory-window-set-item! [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (inventory-window-get-cell inventory-window cell)
        image-widget (.findActor ^Group cell-widget "image-widget")
        cell-size (:cell-size (.getUserObject ^Actor image-widget))]
    (.setDrawable ^Image image-widget ^Drawable (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                                                  (.setMinSize cell-size cell-size)))
    (.addListener ^Actor cell-widget (TextTooltip. ^String tooltip-text ^Skin skin))
    nil))

(defn- ui-set-item! [skin stage textures cell item]
  (-> (.getRoot ^Stage stage)
      (.findActor "moon.ui.windows.inventory")
      (inventory-window-set-item! cell
                                  {:texture-region (textures/texture-region textures (:entity/image item))
                                   :tooltip-text (item/info-text item)}
                                  skin)))

(defn- ui-set-skill! [skin stage textures elapsed-time skill]
  (-> (.getRoot ^Stage stage)
      (.findActor "moon.ui.action-bar")
      (action-bar-add-skill! {:skill-id (:property/id skill)
                              :texture-region (textures/texture-region textures (:entity/image skill))
                              :tooltip-text (info-text skill elapsed-time)}
                             skin)))

(defn after-create-component
  [ui-set-skill! ui-set-item! elapsed-time eid [k v]]
  (case k
    :entity/fsm
    (let [{:keys [fsm initial-state]} v]
      (swap! eid assoc :entity/fsm (create-fsm fsm initial-state))
      (swap! eid assoc initial-state (create-entity-state [initial-state nil] eid elapsed-time))
      nil)

    :entity/skills
    (do
      (swap! eid assoc :entity/skills nil)
      (doseq [{:keys [property/id] :as skill} v]
        (assert (not (contains? (:entity/skills @eid) id)))
        (swap! eid update :entity/skills assoc id skill)
        (when (:entity/player? @eid)
          (ui-set-skill! skill)))
      nil)

    :entity/inventory
    (do
      (swap! eid assoc :entity/inventory (->> #:inventory.slot{:bag      [6 4]
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
                                                 (into {})))
      (doseq [item v]
        (assert (item/valid? item))
        (let [[cell cell-item] (inventory/can-pickup-item? (:entity/inventory @eid) item)]
          (assert cell)
          (assert (nil? cell-item))
          (swap! eid set-item cell item)
          (when (:entity/player? @eid)
            (ui-set-item! cell item))))
      nil)

    nil))

(defn spawn-entity! [world elapsed-time skin stage textures z-orders minimum-size entity]
  (let [elapsed-time* @elapsed-time
        entity (reduce (fn [m [k v]]
                         (assoc m k (create-component elapsed-time* k v)))
                       {}
                       entity)
        entity (let [{:entity/keys [position width height collides? z-order rotation-angle]} entity]
                 (assert position)
                 (assert width)
                 (assert height)
                 (assert (>= width  (if collides? minimum-size 0)))
                 (assert (>= height (if collides? minimum-size 0)))
                 (assert (or (boolean? collides?) (nil? collides?)))
                 (assert ((set z-orders) z-order))
                 (assert (or (nil? rotation-angle)
                             (<= 0 rotation-angle 360)))
                 (assoc entity
                        :entity/position (mapv float position)
                        :entity/width (float width)
                        :entity/height (float height)
                        :entity/rotation-angle (or rotation-angle 0)))
        eid (atom entity)]
    (register-eid! @world eid)
    (doseq [component @eid]
      (after-create-component #(ui-set-skill! skin stage textures elapsed-time* %)
                              #(ui-set-item! skin stage textures %1 %2)
                              elapsed-time*
                              eid
                              component))))
