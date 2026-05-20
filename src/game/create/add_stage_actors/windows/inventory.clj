(ns game.create.add-stage-actors.windows.inventory
  (:require [gdl.scene2d.event :as event]
            [gdl.scene2d.group :as group]
            [gdl.scene2d.ui.image :as image]
            [gdl.scene2d.actor :as actor]
            [moon.draws :as draws]
            [moon.inventory :as inventory]
            [moon.state :as state]
            [moon.txs :as txs]
            [moon.textures :as textures]
            moon.ui.inventory-window))

(defmethod state/clicked-inventory-cell :player-idle
  [_ eid cell]
  (when-let [item (get-in (:entity/inventory @eid) cell)]
    [[:tx/sound "bfxr_takeit"]
     [:tx/event eid :pickup-item item]
     [:tx/remove-item eid cell]]))

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
                                                           :image/bounds bounds})))
        cell-size 48
        slot->drawable (fn [slot]
                         {:drawable/texture-region (slot->texture-region slot)
                          :drawable/size cell-size
                          :drawable/tint [1 1 1 0.4]})
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
                    (actor/create
                     {:type :ui/stack
                      :actor/name "inventory-cell"
                      :actor/user-object cell
                      :actor/listeners {:listener/click (fn [event _x _y]
                                                          (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (event/stage event))
                                                                entity @player-eid
                                                                state-k (:state (:entity/fsm entity))]
                                                            (txs/handle! ctx
                                                                         (state/clicked-inventory-cell [state-k (state-k entity)]
                                                                                                       player-eid
                                                                                                       cell))))}
                      :group/actors [{:type :ui/widget
                                      :draw! (fn [this _batch _parent-alpha]
                                               (when-let [stage (actor/stage this)]
                                                 (let [{:keys [ctx/player-eid
                                                               ctx/ui-mouse-position]
                                                        :as ctx} (:stage/ctx stage)]
                                                   (draws/handle ctx
                                                                 (draw-cell-rect @player-eid
                                                                                 (actor/x this)
                                                                                 (actor/y this)
                                                                                 (actor/hit this
                                                                                            (actor/stage->local-coordinates this ui-mouse-position)
                                                                                            true)
                                                                                 (actor/user-object (actor/parent this)))))))}
                                     {:type :ui/image
                                      :content background-drawable
                                      :actor/name "image-widget"
                                      :actor/user-object {:background-drawable background-drawable
                                                          :cell-size cell-size}}]})}))]
    {:type :ui/window
     :title "Inventory"
     :skin skin
     :table/rows [[{:actor (actor/create
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
     :actor/position [(:viewport/world-width (:stage/viewport stage))
                      (:viewport/world-height (:stage/viewport stage))]}))

(defn- find-cell [group cell]
  (first (filter #(= (actor/user-object %) cell)
                 (group/children group))))

(defn- window->cell [inventory-window cell]
  (-> inventory-window
      (group/find-actor "inventory-cell-table")
      (find-cell cell)))

(.bindRoot #'moon.ui.inventory-window/set-item!
           (fn [inventory-window cell {:keys [texture-region tooltip-text]} skin]
             (let [cell-widget (window->cell inventory-window cell)
                   image-widget (group/find-actor cell-widget "image-widget")
                   cell-size (:cell-size (actor/user-object image-widget))]
               (image/set-drawable! image-widget {:drawable/texture-region texture-region
                                                  :drawable/size cell-size})
               (actor/add-listener! cell-widget [:listener/text-tooltip [tooltip-text skin]])
               nil)))

(.bindRoot #'moon.ui.inventory-window/remove-item!
           (fn [inventory-window cell]
             (let [cell-widget (window->cell inventory-window cell)
                   image-widget (group/find-actor cell-widget "image-widget")]
               (image/set-drawable! image-widget (:background-drawable (actor/user-object image-widget)))
               ; !! TODO FIXME FIXME FIXME !!!
               ;(.removeListener actor (.getListeners actor))
               ; ... first find the listener
               #_(tooltip/remove! cell-widget)
               nil)))
