(ns create.add-stage-actors
  (:require [game.ctx :as ctx]
            [game.info :as info]
            [game.state :as state]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.event :as event]
            [gdx.scenes.scene2d.group :as group]
            [gdx.scenes.scene2d.stage :as stage]
            [gdx.scenes.scene2d.ui.action-bar :as action-bar]
            [gdx.scenes.scene2d.ui.image :as image]
            [gdx.scenes.scene2d.ui.info-window :as info-window]
            [gdx.scenes.scene2d.ui.stack :as stack]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.widget :as widget]
            [gdx.scenes.scene2d.ui.window :as window]
            [gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.inventory :as inventory]
            [moon.textures :as textures]
            [stage.dev-menu :as dev-menu]
            [stage.hp-mana-bar :as hp-mana-bar]))



(defn create-player-state-draw []
  (actor/create
   {:draw! (fn [this _batch _parent-alpha]
             (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (actor/stage this))
                   entity @player-eid
                   state-k (:state (:entity/fsm entity))]
               (ctx/draw! ctx (state/draw-ui-view [state-k (state-k entity)] player-eid ctx))))}))

(defn create-player-message-actor []
  (let [message-duration-seconds 0.5]
    (actor/create
     {:actor/name "player-message"
      :actor/user-object (atom nil)
      :draw! (fn [this _batch _parent-alpha]
               (when-let [stage (actor/stage this)]
                 (ctx/draw! (:stage/ctx stage)
                            [(let [state (actor/user-object this)
                                   vp-width (:viewport/world-width (:stage/viewport stage))
                                   vp-height (:viewport/world-height (:stage/viewport stage))]
                               (when-let [text (:text @state)]
                                 [:draw/text {:x (/ vp-width 2)
                                              :y (+ (/ vp-height 2) 200)
                                              :text text
                                              :scale 2.5
                                              :up? true}]))])))
      :act! (fn [this delta]
              (let [state (actor/user-object this)]
                (when (:text @state)
                  (swap! state update :counter + delta)
                  (when (>= (:counter @state) message-duration-seconds)
                    (reset! state nil)))))})))

(defn create-info-window
  [{:keys [ctx/skin
           ctx/stage]}]
  (info-window/create
   {:title "Entity Info"
    :actor-name "moon.ui.windows.entity-info"
    :visible? false
    :position [(:viewport/world-width (:stage/viewport stage)) 0]
    :set-label-text! (fn [{:keys [ctx/mouseover-eid]
                           :as ctx}]
                       (if-let [eid mouseover-eid]
                         (info/text (apply dissoc @eid [:entity/skills
                                                        :entity/faction
                                                        :active-skill])
                                    ctx)
                         ""))
    :skin skin}))

(defn create-inventory-window
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
                         (texture-region-drawable/create
                          {:drawable/texture-region (slot->texture-region slot)
                           :drawable/size cell-size
                           :drawable/tint [1 1 1 0.4]}))
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
      :actor/position [(:viewport/world-width (:stage/viewport stage))
                       (:viewport/world-height (:stage/viewport stage))]})))

(defn step
  [{:keys [ctx/stage]
    :as ctx}]
  (doseq [actor [(dev-menu/create ctx)
                 (action-bar/create)
                 (hp-mana-bar/create ctx)
                 (group/create
                  {:group/actors [(create-info-window ctx)
                                  (create-inventory-window ctx)]
                   :actor/name "moon.ui.windows"})
                 (create-player-state-draw)
                 (create-player-message-actor)]]
    (stage/add-actor! stage actor))
  ctx)
