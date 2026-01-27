(ns moon.ui-actors.windows.inventory
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color]
            [moon.ctx :as ctx]
            [moon.entity.state :as state]
            [moon.inventory :as inventory]
            [moon.textures :as textures]
            [moon.ui :as ui])
  (:import (com.badlogic.gdx.graphics Color)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            Event)
           (com.badlogic.gdx.scenes.scene2d.ui Image
                                               Widget)
           (com.badlogic.gdx.scenes.scene2d.utils ClickListener
                                                  TextureRegionDrawable)
           (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.utils.viewport Viewport)
           (moon Stage)))

(defn- clicked-inventory-cell [cell {:keys [ctx/player-eid] :as ctx}]
  (let [entity @player-eid
        state-k (:state (:entity/fsm entity))]
    (ctx/handle! ctx
                 (state/clicked-inventory-cell [state-k (state-k entity)]
                                               player-eid
                                               cell))))

(defn- draw-cell-rect-actor [draw-cell-rect]
  (proxy [Widget] []
    (draw [_batch _parent-alpha]
      (when-let [stage (Actor/.getStage this)]
        (let [{:keys [ctx/player-eid
                      ctx/ui-mouse-position]
               :as ctx} (.ctx ^Stage stage)]
          (ctx/draw! ctx
                     (let [[x y] ui-mouse-position]
                       (draw-cell-rect @player-eid
                                       (Actor/.getX this)
                                       (Actor/.getY this)
                                       (let [v2 (Actor/.stageToLocalCoordinates this (Vector2. x y))]
                                         (Actor/.hit this (.x v2) (.y v2) true))
                                       (Actor/.getUserObject (Actor/.getParent this))))))))))

(defn- create-inventory-window*
  [{:keys [position
           title
           actor/visible?
           clicked-cell-listener
           slot->texture-region
           skin]}]
  (let [cell-size 48
        slot->drawable (fn [slot]
                         (doto (TextureRegionDrawable. ^TextureRegion (slot->texture-region slot))
                           (.setMinSize cell-size cell-size)
                           (.tint (Color. 1 1 1 0.4))))
        droppable-color   (color/float-bits [0   0.6 0 0.8 1])
        not-allowed-color (color/float-bits [0.6 0   0 0.8 1])
        draw-cell-rect (fn [player-entity x y mouseover? cell]
                         [[:draw/rectangle x y cell-size cell-size (color/float-bits [0.5 0.5 0.5 1])]
                          (when (and mouseover?
                                     (= :player-item-on-cursor (:state (:entity/fsm player-entity))))
                            (let [item (:entity/item-on-cursor player-entity)
                                  color (if (inventory/valid-slot? cell item)
                                          droppable-color
                                          not-allowed-color)]
                              [:draw/filled-rectangle (inc x) (inc y) (- cell-size 2) (- cell-size 2) color]))])
        ->cell (fn [slot & {:keys [position]}]
                 (let [cell [slot (or position [0 0])]
                       background-drawable (slot->drawable slot)]
                   {:actor (ui/actor
                            {:type :ui/stack
                             :actor/name "inventory-cell"
                             :actor/user-object cell
                             :actor/listener (clicked-cell-listener cell)
                             :group/actors [(draw-cell-rect-actor draw-cell-rect)
                                            (doto (Image. ^TextureRegionDrawable background-drawable)
                                              (.setName "image-widget")
                                              (.setUserObject {:background-drawable background-drawable
                                                               :cell-size cell-size}))]})}))]
    (ui/actor
     {:type :ui/window
      :skin skin
      :title title
      :actor/name "moon.ui.windows.inventory"
      :actor/visible? visible?
      :pack? true
      :actor/position position
      :rows [[{:actor (ui/actor
                       {:type :ui/table
                        :actor/name "inventory-cell-table"
                        :rows (concat [[nil nil
                                        (->cell :inventory.slot/helm)
                                        (->cell :inventory.slot/necklace)]
                                       [nil
                                        (->cell :inventory.slot/weapon)
                                        (->cell :inventory.slot/chest)
                                        (->cell :inventory.slot/cloak)
                                        (->cell :inventory.slot/shield)]
                                       [nil nil
                                        (->cell :inventory.slot/leg)]
                                       [nil
                                        (->cell :inventory.slot/glove)
                                        (->cell :inventory.slot/rings :position [0 0])
                                        (->cell :inventory.slot/rings :position [1 0])
                                        (->cell :inventory.slot/boot)]]
                                      (for [y (range 4)]
                                        (for [x (range 6)]
                                          (->cell :inventory.slot/bag :position [x y]))))})
               :pad 4}]]})))

(defn create
  [{:keys [ctx/skin
           ctx/stage
           ctx/textures]}]
  (let [slot->y-sprite-idx #:inventory.slot {:weapon   0
                                             :shield   1
                                             :rings    2
                                             :necklace 3
                                             :helm     4
                                             :cloak    5
                                             :chest    6
                                             :leg      7
                                             :glove    8
                                             :boot     9
                                             :bag      10}
        slot->texture-region (fn [slot]
                               (let [width  48
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
    (create-inventory-window*
     {:skin skin
      :title "Inventory"
      :actor/visible? false
      :position [(Viewport/.getWorldWidth  (Stage/.getViewport stage))
                 (Viewport/.getWorldHeight (Stage/.getViewport stage))]
      :clicked-cell-listener (fn [cell]
                               (proxy [ClickListener] []
                                 (clicked [event x y]
                                   (clicked-inventory-cell cell (.ctx ^Stage (Event/.getStage event))))))
      :slot->texture-region slot->texture-region})))
