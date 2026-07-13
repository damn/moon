(ns moon.inventory-window
  (:require [gdx.color :as color]
            [gdx.actor :as actor]
            [gdx.event :as event]
            [gdx.actor.group :as group]
            [gdx.actor.widget.image :as image]
            [gdx.actor.group.widget.stack :as stack]
            [gdx.tooltip.text :as text-tooltip]
            [gdx.actor.widget :as widget]
            [gdx.actor.group.widget.table.window :as window]
            [gdx.click-listener :as click-listener]
            [gdx.drawable.texture-region :as texture-region-drawable]
            [gdx.actor.group.widget.table :as table]
            [gdx.vector2 :as vector2]
            [moon.inventory.cell :as inventory-cell]))

(defn- get-cell [inventory-window cell]
  (->> "inventory-cell-table"
       (#(group/find-actor inventory-window %))
       group/get-children
       (filter #(= (actor/get-user-object %) cell))
       first))

(defn remove-item! [inventory-window cell]
  (let [cell-widget (get-cell inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")]
    (image/set-drawable! image-widget (:background-drawable (actor/get-user-object image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))

(defn set-item! [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (get-cell inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")
        cell-size (:cell-size (actor/get-user-object image-widget))]
    (image/set-drawable! image-widget (doto (texture-region-drawable/create texture-region)
                                        (texture-region-drawable/set-min-size! cell-size cell-size)))
    (actor/add-listener! cell-widget (text-tooltip/create tooltip-text skin))
    nil))

(defn- ->cell [do! draw! on-click-cell slot->drawable draw-cell-rect cell-size slot & {:keys [position]}]
  (let [cell [slot (or position [0 0])]
        background-drawable (slot->drawable slot)]
    {:actor
     (let [stack (stack/create)]
       (run! #(group/add-actor! stack %)
             [(widget/new
               (fn [this _batch _parent-alpha]
                 (when-let [stage (actor/get-stage this)]
                   (let [{:keys [ctx/player-eid
                                 ctx/ui-mouse-position]
                          :as ctx} (:stage/ctx stage)]
                     (draw! ctx
                            (draw-cell-rect @player-eid
                                            (actor/get-x this)
                                            (actor/get-y this)
                                            (let [[x y] (vector2/clojurize
                                                         (actor/stage-to-local-coordinates this
                                                                                          (vector2/new ui-mouse-position)))]
                                              (actor/hit this x y true))
                                            (actor/get-user-object (actor/get-parent this))))))))
              (doto (image/create-drawable background-drawable)
                (actor/set-name! "image-widget")
                (actor/set-user-object! {:background-drawable background-drawable
                                      :cell-size cell-size}))])
       (doto stack
         (actor/add-listener! (click-listener/create
                             (fn [event _x _y]
                               (let [{:keys [ctx/player-eid]
                                      :as ctx} (:stage/ctx (event/get-stage event))]
                                 (do! ctx (on-click-cell ctx player-eid cell))))))
         (actor/set-name! "inventory-cell")
         (actor/set-user-object! cell)))}))

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
                         (doto (texture-region-drawable/create (slot->texture-region slot))
                           (texture-region-drawable/set-min-size! cell-size cell-size)
                           (texture-region-drawable/tint! (color/create [1 1 1 0.4]))))
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
        window (doto (window/create {:title "Inventory"
                                     :skin skin
                                     :table/rows [[{:actor (doto (table/create
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
                                                                                          (->cell :inventory.slot/bag :position [x y]))))})
                                                                  (actor/set-name! "inventory-cell-table"))
                                                    :pad 4}]]})
                     (actor/set-name! "moon.ui.windows.inventory")
                     (actor/set-visible! false))]
    (let [[x y] position]
      (actor/set-position! window x y))
    window))
