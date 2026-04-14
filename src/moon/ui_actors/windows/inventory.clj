(ns moon.ui-actors.windows.inventory
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color]
            [gdl.scene2d.event :as event]
            [gdl.scene2d.group :as group]
            [gdl.scene2d.ui.image :as image]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.drawable :as drawable]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [gdl.viewport :as viewport]
            [moon.actor :as actor]
            [moon.draws :as draws]
            [moon.inventory :as inventory]
            [moon.inventory-window]
            [moon.stage :as stage]
            [moon.state :as state]
            [moon.textures :as textures]
            [moon.txs :as txs]
            [moon.ui :as ui]))

(defn- clicked-inventory-cell [cell {:keys [ctx/player-eid] :as ctx}]
  (let [entity @player-eid
        state-k (:state (:entity/fsm entity))]
    (txs/handle! ctx
                 (state/clicked-inventory-cell [state-k (state-k entity)]
                                               player-eid
                                               cell))))

(defn- draw-cell-rect-actor [draw-cell-rect]
  {:type :ui/widget
   :draw! (fn [this _batch _parent-alpha]
            (when-let [stage (actor/stage this)]
              (let [{:keys [ctx/player-eid
                            ctx/ui-mouse-position]
                     :as ctx} (stage/ctx stage)]
                (draws/handle! ctx
                               (draw-cell-rect @player-eid
                                               (actor/x this)
                                               (actor/y this)
                                               (actor/hit this
                                                          (actor/stage->local-coordinates this ui-mouse-position)
                                                          true)
                                               (actor/user-object (actor/parent this)))))))})

(defn- create-drawable
  [{:keys [drawable/texture-region drawable/min-size drawable/tint]}]
  (doto (texture-region-drawable/create texture-region)
    (drawable/set-min-size! (min-size 0) (min-size 1))
    (texture-region-drawable/tint (color/create tint))))

(defn- create-inventory-window*
  [{:keys [colors
           position
           title
           clicked-cell-listener
           slot->texture-region
           skin]}]
  (let [cell-size 48
        slot->drawable (fn [slot]
                         {
                          :drawable/texture-region (slot->texture-region slot)
                          :drawable/min-size [cell-size cell-size]
                          :drawable/tint [1 1 1 0.4]
                          }
                         )
        draw-cell-rect (fn [player-entity x y mouseover? cell]
                         [[:draw/rectangle x y cell-size cell-size (:colors/item-rect colors)]
                          (when (and mouseover?
                                     (= :player-item-on-cursor (:state (:entity/fsm player-entity))))
                            (let [item (:entity/item-on-cursor player-entity)
                                  color (if (inventory/valid-slot? cell item)
                                          (:colors/droppable-item colors)
                                          (:colors/not-allowed-drop-item colors))]
                              [:draw/filled-rectangle (inc x) (inc y) (- cell-size 2) (- cell-size 2) color]))])
        ->cell (fn [slot & {:keys [position]}]
                 (let [cell [slot (or position [0 0])]
                       background-drawable (slot->drawable slot)]
                   {:actor
                    (ui/create
                     {:type :ui/stack
                      :actor/name "inventory-cell"
                      :actor/user-object cell
                      :actor/listeners {:listener/click (clicked-cell-listener cell)}
                      :group/actors [(ui/create
                                      (draw-cell-rect-actor draw-cell-rect))
                                     (ui/create
                                      {:type :ui/image
                                       :content (create-drawable background-drawable)
                                       :actor/name "image-widget"
                                       :actor/user-object {:background-drawable background-drawable
                                                           :cell-size cell-size}})]})}))]
    (ui/create
     {:type :ui/window
      :title title
      :skin skin
      :table/rows [[{:actor (ui/create
                             {:type :ui/table
                              :actor/name "inventory-cell-table"
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

(defn create
  [{:keys [ctx/colors
           ctx/skin
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
     {:colors colors
      :skin skin
      :title "Inventory"
      :position [(viewport/world-width  (stage/viewport stage))
                 (viewport/world-height (stage/viewport stage))]
      :clicked-cell-listener (fn [cell]
                               (fn [event _x _y]
                                 (clicked-inventory-cell cell (stage/ctx (event/stage event)))))
      :slot->texture-region slot->texture-region})))

(defn- find-cell [group cell]
  (first (filter #(= (actor/user-object %) cell)
                 (group/children group))))

(defn- window->cell [inventory-window cell]
  (-> inventory-window
      (group/find-actor "inventory-cell-table")
      (find-cell cell)))

(extend-type com.badlogic.gdx.scenes.scene2d.ui.Window
  moon.inventory-window/InventoryWindow
  (set-item! [inventory-window cell {:keys [texture-region tooltip-text]} skin]
    (let [cell-widget (window->cell inventory-window cell)
          image-widget (group/find-actor cell-widget "image-widget")
          cell-size (:cell-size (actor/user-object image-widget))
          drawable (doto (texture-region-drawable/create texture-region)
                     (drawable/set-min-size! cell-size cell-size))]
      (image/set-drawable! image-widget drawable)
      (actor/add-listener! cell-widget [:listener/text-tooltip [tooltip-text skin]])
      nil))

  (remove-item! [inventory-window cell]
    (let [cell-widget (window->cell inventory-window cell)
          image-widget (group/find-actor cell-widget "image-widget")]
      (image/set-drawable! image-widget (create-drawable (:background-drawable (actor/user-object image-widget))))
      ; !! TODO FIXME FIXME FIXME !!!
      ;(.removeListener actor (.getListeners actor))
      ; ... first find the listener
      #_(tooltip/remove! cell-widget)
      nil)))
