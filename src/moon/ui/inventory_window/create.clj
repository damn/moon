(ns moon.ui.inventory-window.create
  (:require [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.scenes.scene2d.ui.widget :as widget]
            [game.ctx :as ctx]
            [game.state :as state]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.ui.image :as image]
            [gdx.scenes.scene2d.ui.image :as image]
            [gdx.scenes.scene2d.ui.stack :as stack]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.window :as window]
            [gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.inventory :as inventory]))

(defn f
  [{:keys [item-rect-color
           droppable-item-color
           not-allowed-drop-item-color
           skin
           position
           slot->texture-region
           cell-size]}]
  (let [slot->drawable (fn [slot]
                         (texture-region-drawable/create
                          {:drawable/texture-region (slot->texture-region slot)
                           :drawable/size cell-size
                           :drawable/tint [1 1 1 0.4]}))
        draw-cell-rect (fn [player-entity x y mouseover? cell]
                         [[:draw/rectangle x y cell-size cell-size item-rect-color]
                          (when (and mouseover?
                                     (= :player-item-on-cursor (:state (:entity/fsm player-entity))))
                            (let [item (:entity/item-on-cursor player-entity)
                                  color (if (inventory/valid-slot? cell item)
                                          droppable-item-color
                                          not-allowed-drop-item-color)]
                              [:draw/filled-rectangle (inc x) (inc y) (- cell-size 2) (- cell-size 2) color]))])
        ->cell (fn [slot & {:keys [position]}]
                 (let [cell [slot (or position [0 0])]
                       background-drawable (slot->drawable slot)]
                   {:actor
                    (stack/create
                     {:actor/name "inventory-cell"
                      :actor/user-object cell
                      :actor/listeners {:listener/click (fn [event _x _y]
                                                          (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (event/stage event))
                                                                entity @player-eid
                                                                state-k (:state (:entity/fsm entity))]
                                                            (ctx/do! ctx
                                                                     (state/clicked-inventory-cell [state-k (state-k entity)]
                                                                                                   player-eid
                                                                                                   cell))))}
                      :group/actors [(widget/create
                                      {:draw! (fn [this _batch _parent-alpha]
                                                (when-let [stage (actor/stage this)]
                                                  (let [{:keys [ctx/player-eid
                                                                ctx/ui-mouse-position]
                                                         :as ctx} (:stage/ctx stage)]
                                                    (ctx/draw! ctx
                                                               (draw-cell-rect @player-eid
                                                                               (actor/x this)
                                                                               (actor/y this)
                                                                               (actor/hit this
                                                                                          (actor/stage->local-coordinates this ui-mouse-position)
                                                                                          true)
                                                                               (actor/user-object (actor/parent this)))))))})
                                     (image/create
                                      {:content background-drawable
                                       :actor/name "image-widget"
                                       :actor/user-object {:background-drawable background-drawable
                                                           :cell-size cell-size}})]})}))]
    (window/create
     {:title "Inventory"
      :skin skin
      :table/rows [[{:actor (table/create
                             {:actor/name "inventory-cell-table"
                              :table/rows (concat [[nil nil
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
                     :pad 4}]]
      :actor/name "moon.ui.windows.inventory"
      :actor/visible? false
      :actor/position position})))
