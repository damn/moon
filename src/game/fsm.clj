(ns game.fsm
  (:require [entity.spawn :refer [create-entity-state spawn-entity!]]
            [game.audio :refer [play-sound!]]
            [game.entity :refer [add-text-effect]]
            [game.spawn :refer [item-place-position spawn-alert spawn-item]]
            [moon.stats :as stats]
            [moon.timer :as timer]
            [reduce-fsm :as fsm])
  (:import (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Label Skin TextButton Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)
           (com.badlogic.gdx.utils Align)))

(defn- show-modal! [^Skin skin ^Stage stage {:keys [title text button-text on-click]}]
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

(defn handle-fsm-event! [world elapsed-time audio skin stage textures z-orders minimum-size eid world-mouse-position event & [params]]
  (let [elapsed-time* @elapsed-time
        fsm (:entity/fsm @eid)
        _ (assert fsm)
        old-state-k (:state fsm)
        new-fsm (fsm/fsm-event fsm event)
        new-state-k (:state new-fsm)]
    (when-not (= old-state-k new-state-k)
      (let [old-state-obj (let [k (:state (:entity/fsm @eid))]
                             [k (k @eid)])
            state-args (if params [new-state-k params] [new-state-k nil])
            new-state-obj [new-state-k (create-entity-state state-args eid elapsed-time*)]]
        (swap! eid assoc :entity/fsm new-fsm)
        (swap! eid assoc new-state-k (new-state-obj 1))
        (swap! eid dissoc old-state-k)
        (let [[state-k _state-v] old-state-obj]
          (case state-k
            :player-item-on-cursor
            (let [entity @eid
                  item (:entity/item-on-cursor entity)]
              (when item
                (swap! eid dissoc :entity/item-on-cursor)
                (play-sound! audio "bfxr_itemputground")
                (spawn-entity! world elapsed-time skin stage textures z-orders minimum-size
                               (spawn-item (item-place-position (:entity/position entity)
                                                                world-mouse-position
                                                                (- (:entity/click-distance-tiles entity) 0.1))
                                           item))))

            :player-moving
            (do (swap! eid dissoc :entity/movement)
                nil)

            :npc-sleeping
            (do (swap! eid add-text-effect elapsed-time* "[WHITE]!" 1)
                (spawn-entity! world elapsed-time skin stage textures z-orders minimum-size
                               (spawn-alert (:entity/position @eid) (:entity/faction @eid) 0.2 elapsed-time*)))

            :npc-moving
            (do (swap! eid dissoc :entity/movement)
                nil)

            nil))
        (let [[state-k state-v] new-state-obj]
          (case state-k
            :player-item-on-cursor
            (let [{:keys [item]} state-v]
              (swap! eid assoc :entity/item-on-cursor item)
              nil)

            :active-skill
            (let [{:keys [skill]} state-v]
              (swap! eid update :entity/stats stats/pay-mana-cost (:skill/cost skill))
              (swap! eid assoc-in [:entity/skills (:property/id skill) :skill/cooling-down?]
                     (timer/create elapsed-time* (:skill/cooldown skill)))
              (play-sound! audio (:skill/start-action-sound skill))
              nil)

            :npc-dead
            (do (swap! eid assoc :entity/destroyed? true)
                nil)

            :player-moving
            (let [{:keys [movement-vector]} state-v]
              (swap! eid assoc :entity/movement {:direction movement-vector
                                                 :speed (or (stats/get-value (:entity/stats @eid) :stats/movement-speed)
                                                            0)})
              nil)

            :player-dead
            (do (play-sound! audio "bfxr_playerdeath")
                (show-modal! skin stage {:title "YOU DIED - again!"
                                         :text "Good luck next time!"
                                         :button-text "OK"
                                         :on-click (fn [])})
                nil)

            :npc-moving
            (let [{:keys [movement-vector]} state-v]
              (swap! eid assoc :entity/movement {:direction movement-vector
                                                 :speed (or (stats/get-value (:entity/stats @eid) :stats/movement-speed)
                                                            0)})
              nil)

            nil))
        nil))))
