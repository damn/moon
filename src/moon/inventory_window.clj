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

(defn- get-player-eid [ctx]
  (:ctx/player-eid ctx))

(defn- get-ui-mouse-position [ctx]
  (:ctx/ui-mouse-position ctx))

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

(defn- ->cell [on-click-cell slot->drawable draw-cell-rect! cell-size slot & {:keys [position]}]
  (let [cell [slot (or position [0 0])]
        background-drawable (slot->drawable slot)]
    {:actor
     (let [stack (stack/create)]
       (run! #(group/add-actor! stack %)
             [(widget/new
               (fn [this _batch _parent-alpha]
                 (when-let [stage (actor/get-stage this)]
                   (let [ctx (:stage/ctx stage)]
                     (draw-cell-rect! ctx
                                      @(get-player-eid ctx)
                                      (actor/get-x this)
                                      (actor/get-y this)
                                      (let [[x y] (vector2/clojurize
                                                   (actor/stage-to-local-coordinates this
                                                                                    (vector2/new (get-ui-mouse-position ctx))))]
                                        (actor/hit this x y true))
                                      (actor/get-user-object (actor/get-parent this)))))))
              (doto (image/create-drawable background-drawable)
                (actor/set-name! "image-widget")
                (actor/set-user-object! {:background-drawable background-drawable
                                      :cell-size cell-size}))])
       (doto stack
         (actor/add-listener! (click-listener/create
                             (fn [event _x _y]
                               (let [ctx (:stage/ctx (event/get-stage event))]
                                 (on-click-cell ctx (get-player-eid ctx) cell)))))
         (actor/set-name! "inventory-cell")
         (actor/set-user-object! cell)))}))

(defn inventory-window-build
  [{:keys [on-click-cell
           draw-cell-rect!
           skin
           position
           slot->texture-region
           cell-size]}]
  (let [slot->drawable (fn [slot]
                         (doto (texture-region-drawable/create (slot->texture-region slot))
                           (texture-region-drawable/set-min-size! cell-size cell-size)
                           (texture-region-drawable/tint! (color/create [1 1 1 0.4]))))
        ->cell (partial ->cell on-click-cell slot->drawable draw-cell-rect! cell-size)
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
