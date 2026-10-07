(ns game.listener.create.ui-actors
  (:require [game.listener.create.ui-actors.action-bar :refer [create-action-bar]]
            [game.listener.create.ui-actors.dev-menu :refer [create-dev-menu dev-menus]]
            [game.listener.create.ui-actors.entity-info :refer [stage-info-window-create]]
            [game.listener.create.ui-actors.hp-mana-bar :refer [hp-mana-bar-create]]
            [game.listener.create.ui-actors.inventory-window :refer [handle-clicked-inventory-cell
                                                                     inventory-window-create]]
            [game.listener.create.ui-actors.player-message :refer [player-message-actor-create]]
            [game.listener.create.ui-actors.player-state-draw :refer [player-state-draw-create]]
            [game.listener.create.ui-actors.windows :refer [windows-create]]
            [game.shared :refer [colors
                                 draw-fn-filled-rectangle
                                 draw-fn-rectangle
                                 ui-mouse-position
                                 ui-remove-item!
                                 ui-set-item!
                                 world-mouse-position]]
            [moon.inventory :as inventory]
            [moon.number :as number])
  (:import (com.badlogic.gdx Gdx Graphics)
           (com.badlogic.gdx.graphics OrthographicCamera)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn create-ui-actors
  [world elapsed-time mouseover-eid paused? player-eid
   show-tile-grid? show-cell-entities? show-cell-occupied? show-body-bounds? show-potential-field-colors?
   unit-scale
   z-orders
   minimum-size
   audio default-font shape-drawer skin stage textures* world-viewport]
  (let [cell-size 48]
    [(create-action-bar)
     (create-dev-menu
      {:menus (dev-menus show-tile-grid? show-cell-entities? show-cell-occupied? show-body-bounds? show-potential-field-colors?)
       :update-labels (for [item [{:label "elapsed-time"
                                   :update-fn (fn []
                                                (str (number/readable @elapsed-time) " seconds"))
                                   :icon "images/clock.png"}
                                  {:label "FPS"
                                   :update-fn (fn [] (.getFramesPerSecond ^Graphics Gdx/graphics))
                                   :icon "images/fps.png"}
                                  {:label "Mouseover-entity id"
                                   :update-fn (fn []
                                                (when-let [entity (and @mouseover-eid @@mouseover-eid)]
                                                  (:entity/id entity)))
                                   :icon "images/mouseover.png"}
                                  {:label "paused?"
                                   :update-fn (fn [] @paused?)}
                                  {:label "GUI"
                                   :update-fn (fn []
                                                (mapv int (ui-mouse-position stage)))}
                                  {:label "World"
                                   :update-fn (fn []
                                                (mapv int (world-mouse-position world-viewport)))}
                                  {:label "Zoom"
                                   :update-fn (fn []
                                                (.zoom ^OrthographicCamera (.getCamera ^Viewport world-viewport)))
                                   :icon "images/zoom.png"}]]
                        (if (:icon item)
                          (update item :icon #(get textures* %))
                          item))
       :skin skin})
     (hp-mana-bar-create default-font stage textures* unit-scale player-eid)
     (windows-create [#(stage-info-window-create skin stage mouseover-eid elapsed-time)
                      #(inventory-window-create
                        (fn [_event cell]
                          (handle-clicked-inventory-cell world
                                                         elapsed-time
                                                         @player-eid
                                                         audio
                                                         skin
                                                         stage
                                                         textures*
                                                         z-orders
                                                         minimum-size
                                                         (fn [cell item] (ui-set-item! skin stage textures* cell item))
                                                         (fn [cell] (ui-remove-item! stage cell))
                                                         cell
                                                         (world-mouse-position world-viewport)))
                        (fn [player-entity x y mouseover? cell]
                          (draw-fn-rectangle shape-drawer x y cell-size cell-size (:colors/item-rect colors))
                          (when (and mouseover?
                                     (= :player-item-on-cursor (:state (:entity/fsm player-entity))))
                            (let [item (:entity/item-on-cursor player-entity)
                                  color (if (inventory/valid-slot? cell item)
                                          (:colors/droppable-item colors)
                                          (:colors/not-allowed-drop-item colors))]
                              (draw-fn-filled-rectangle shape-drawer (inc x) (inc y) (- cell-size 2) (- cell-size 2) color))))
                        skin
                        stage
                        textures*
                        player-eid)])
     (player-state-draw-create unit-scale textures* player-eid)
     (player-message-actor-create default-font unit-scale)]))
