(ns cdq.game.create.inventory-window
  (:require [cdq.entity.inventory :as inventory]
            [cdq.graphics :as graphics]
            [cdq.ui :as ui]
            [cdq.ui.image :as image]
            [cdq.ui.stack :as stack]
            [cdq.ui.stage :as stage]
            [cdq.ui.table :as table]
            [cdq.ui.window :as window]
            [clojure.gdx.graphics.color :as gdxcolor]
            [clojure.gdx.scene2d.actor :as actor]
            [clojure.gdx.scene2d.event :as event]
            [clojure.gdx.scene2d.ui.widget :as widget]
            [clojure.gdx.scene2d.utils.click-listener :as click-listener]
            [clojure.gdx.scene2d.utils.drawable :as drawable]
            [clojure.gdx.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [clojure.gdx.math.vector2 :as gdxvector2]
            [moon.txs :as txs]))

(let [fn-map {:player-idle           (fn [eid cell]
                                       (when-let [item (get-in (:entity/inventory @eid) cell)]
                                         [[:tx/sound "bfxr_takeit"]
                                          [:tx/event eid :pickup-item item]
                                          [:tx/remove-item eid cell]]))

              :player-item-on-cursor (fn [eid cell]
                                       (let [entity @eid
                                             inventory (:entity/inventory entity)
                                             item-in-cell (get-in inventory cell)
                                             item-on-cursor (:entity/item-on-cursor entity)]
                                         (cond
                                          ; PUT ITEM IN EMPTY CELL
                                          (and (not item-in-cell)
                                               (inventory/valid-slot? cell item-on-cursor))
                                          [[:tx/sound "bfxr_itemput"]
                                           [:tx/dissoc eid :entity/item-on-cursor]
                                           [:tx/set-item eid cell item-on-cursor]
                                           [:tx/event eid :dropped-item]]

                                          ; STACK ITEMS
                                          (and item-in-cell
                                               (inventory/stackable? item-in-cell item-on-cursor))
                                          [[:tx/sound "bfxr_itemput"]
                                           [:tx/dissoc eid :entity/item-on-cursor]
                                           [:tx/stack-item eid cell item-on-cursor]
                                           [:tx/event eid :dropped-item]]

                                          ; SWAP ITEMS
                                          (and item-in-cell
                                               (inventory/valid-slot? cell item-on-cursor))
                                          [[:tx/sound "bfxr_itemput"]
                                           ; need to dissoc and drop otherwise state enter does not trigger picking it up again
                                           ; TODO? coud handle pickup-item from item-on-cursor state also
                                           [:tx/dissoc eid :entity/item-on-cursor]
                                           [:tx/remove-item eid cell]
                                           [:tx/set-item eid cell item-on-cursor]
                                           [:tx/event eid :dropped-item]
                                           [:tx/event eid :pickup-item item-in-cell]])))}]
  (defn state->clicked-inventory-cell [[k v] eid cell]
    (when-let [f (k fn-map)]
      (f eid cell))))

(defn- draw-cell-rect-actor [draw-cell-rect]
  (widget/create
    (fn [this _batch _parent-alpha]
      (when-let [stage (actor/stage this)]
        (let [{:keys [ctx/graphics
                      ctx/world]} (stage/ctx stage)]
          (graphics/draw! graphics
                          (let [ui-mouse (:graphics/ui-mouse-position graphics)]
                            (draw-cell-rect @(:world/player-eid world)
                                            (actor/x this)
                                            (actor/y this)
                                            (let [[x y] (-> this
                                                            (actor/stage->local-coordinates (gdxvector2/->java ui-mouse))
                                                            gdxvector2/->clj)]
                                              (actor/hit this x y true))
                                            (actor/user-object (actor/parent this))))))))))

(defn- create-inventory-window*
  [{:keys [position
           title
           actor/visible?
           clicked-cell-listener
           slot->texture-region]}]
  (let [cell-size 48
        slot->drawable (fn [slot]
                         (doto (texture-region-drawable/create (slot->texture-region slot))
                           (drawable/set-min-size! cell-size cell-size)
                           (texture-region-drawable/tint (gdxcolor/create [1 1 1 0.4]))))
        droppable-color   [0   0.6 0 0.8 1]
        not-allowed-color [0.6 0   0 0.8 1]
        draw-cell-rect (fn [player-entity x y mouseover? cell]
                         [[:draw/rectangle x y cell-size cell-size [0.5 0.5 0.5 1]]
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
                   {:actor (stack/create
                            {:actor/name "inventory-cell"
                             :actor/user-object cell
                             :actor/listener (clicked-cell-listener cell)
                             :group/actors [(draw-cell-rect-actor draw-cell-rect)
                                            (image/create
                                             {:image/object background-drawable
                                              :actor/name "image-widget"
                                              :actor/user-object {:background-drawable background-drawable
                                                                  :cell-size cell-size}})]})}))]
    (window/create
     {:title title
      :actor/name "cdq.ui.windows.inventory"
      :actor/visible? visible?
      :pack? true
      :actor/position position
      :rows [[{:actor (table/create
                       {:actor/name "inventory-cell-table"
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
  [{:keys [ctx/graphics
           ctx/stage]}]
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
                                 (graphics/texture-region graphics
                                                          {:image/file "images/items.png"
                                                           :image/bounds bounds})))]
    (create-inventory-window*
     {:title "Inventory"
      :actor/visible? false
      :position [(ui/viewport-width  stage)
                 (ui/viewport-height stage)]
      :clicked-cell-listener (fn [cell]
                               (click-listener/create
                                (fn [event x y]
                                  (let [{:keys [ctx/world] :as ctx} (stage/ctx (event/stage event))
                                        eid (:world/player-eid world)
                                        entity @eid
                                        state-k (:state (:entity/fsm entity))
                                        txs (state->clicked-inventory-cell [state-k (state-k entity)]
                                                                           eid
                                                                           cell)]
                                    (txs/handle! ctx txs)))))
      :slot->texture-region slot->texture-region})))
