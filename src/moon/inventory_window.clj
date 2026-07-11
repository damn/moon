(ns moon.inventory-window
  (:require [com.badlogic.gdx.graphics.color :as color]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [com.badlogic.gdx.scenes.scene2d.ui.stack :as stack]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [com.badlogic.gdx.scenes.scene2d.ui.widget :as widget]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.utils.click-listener :as click-listener]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.table :as moon-table]
            [gdx.math.vector2 :as vector2]
            [moon.inventory.cell :as inventory-cell]))

(defn- get-cell [inventory-window cell]
  (->> "inventory-cell-table"
       (#(group/findActor inventory-window %))
       group/getChildren
       (filter #(= (actor/getUserObject %) cell))
       first))

(defn remove-item! [inventory-window cell]
  (let [cell-widget (get-cell inventory-window cell)
        image-widget (group/findActor cell-widget "image-widget")]
    (image/setDrawable image-widget (:background-drawable (actor/getUserObject image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))

(defn set-item! [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (get-cell inventory-window cell)
        image-widget (group/findActor cell-widget "image-widget")
        cell-size (:cell-size (actor/getUserObject image-widget))]
    (image/setDrawable image-widget (doto (texture-region-drawable/new texture-region)
                                        (texture-region-drawable/setMinSize cell-size cell-size)))
    (actor/addListener cell-widget (text-tooltip/new tooltip-text skin))
    nil))

(defn- ->cell [do! draw! on-click-cell slot->drawable draw-cell-rect cell-size slot & {:keys [position]}]
  (let [cell [slot (or position [0 0])]
        background-drawable (slot->drawable slot)]
    {:actor
     (let [stack (stack/new)]
       (run! #(group/addActor stack %)
             [(widget/new
               (fn [this _batch _parent-alpha]
                 (when-let [stage (actor/getStage this)]
                   (let [{:keys [ctx/player-eid
                                 ctx/ui-mouse-position]
                          :as ctx} (:stage/ctx stage)]
                     (draw! ctx
                            (draw-cell-rect @player-eid
                                            (actor/getX this)
                                            (actor/getY this)
                                            (let [[x y] (vector2/clojurize
                                                         (actor/stageToLocalCoordinates this
                                                                                          (vector2/new ui-mouse-position)))]
                                              (actor/hit this x y true))
                                            (actor/getUserObject (actor/getParent this))))))))
              (doto (image/newDrawable background-drawable)
                (actor/setName "image-widget")
                (actor/setUserObject {:background-drawable background-drawable
                                      :cell-size cell-size}))])
       (doto stack
         (actor/addListener (click-listener/create
                             (fn [event _x _y]
                               (let [{:keys [ctx/player-eid]
                                      :as ctx} (:stage/ctx (event/getStage event))]
                                 (do! ctx (on-click-cell player-eid cell))))))
         (actor/setName "inventory-cell")
         (actor/setUserObject cell)))}))

(defn inventory-window-build
  [{:keys [do!
           draw!
           on-click-cell
           item-rect-color
           droppable-item-color
           not-allowed-drop-item-color
           skin
           position
           slot->texture-region
           cell-size]}]
  (let [slot->drawable (fn [slot]
                         (doto (texture-region-drawable/new (slot->texture-region slot))
                           (texture-region-drawable/setMinSize cell-size cell-size)
                           (texture-region-drawable/tint (color/new [1 1 1 0.4]))))
        draw-cell-rect (fn [player-entity x y mouseover? cell]
                         [[:draw/rectangle x y cell-size cell-size item-rect-color]
                          (when (and mouseover?
                                     (= :player-item-on-cursor (:state (:entity/fsm player-entity))))
                            (let [item (:entity/item-on-cursor player-entity)
                                  color (if (inventory-cell/valid-slot? cell item)
                                          droppable-item-color
                                          not-allowed-drop-item-color)]
                              [:draw/filled-rectangle (inc x) (inc y) (- cell-size 2) (- cell-size 2) color]))])
        ->cell (partial ->cell do! draw! on-click-cell slot->drawable draw-cell-rect cell-size)
        window (doto (doto (window/new "Inventory" skin)
                           (moon-table/set-opts!
                            {:table/rows [[{:actor (doto (doto (table/new)
                                                               (moon-table/set-opts!
                                                                {:table/rows (concat [[nil nil
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
                                                                                         (->cell :inventory.slot/bag :position [x y]))))}))
                                                          (actor/setName "inventory-cell-table"))
                                           :pad 4}]]}))
                     (actor/setName "moon.ui.windows.inventory")
                     (actor/setVisible false))]
    (let [[x y] position]
      (actor/setPosition window x y))
    window))
