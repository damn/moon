; this is just the create the game step ! ... da haegnt so viel dran ... weird !
(ns moon.create
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [moon.animation]
            [moon.body]
            [moon.ctx :as ctx]
            [moon.db :as db]
            [moon.entity.skills]
            [moon.entity.state :as state]
            [moon.entity.state-impl]
            [moon.entity.state.player-item-on-cursor :as player-item-on-cursor]
            [moon.entity.stats :as stats]
            [moon.graphics :as graphics]                   ; 'creature' ?
            [moon.input :as input]
            [moon.inventory :as inventory]                   ; moon.stats ?
            [moon.timer :as timer]
            [moon.ui :as ui]
            [moon.ui.action-bar :as action-bar]
            [moon.ui.actor :as actor]
            [moon.ui.build.editor-window :as editor-window]
            [moon.ui.dev-menu :as dev-menu]
            [moon.ui.editor.overview-window :as overview-window]
            [moon.ui.editor.widgets-impl]
            [moon.ui.editor.window]
            [moon.ui.group :as group]
            [moon.ui.image :as image]
            [moon.ui.info-window :as info-window]
            [moon.ui.message :as message]
            [moon.ui.stack :as stack]
            [moon.ui.stage :as stage]
            [moon.ui.table :as table]
            [moon.ui.window :as window]
            [moon.utils :as utils]
            [moon.val-max :as val-max]
            [moon.world :as world]
            [moon.world-fns.creature-tiles]
            [moon.world.info :as info]
            [moon.world.tiled-map :as tiled-map]
            [qrecord.core :as q])
  (:import (com.badlogic.gdx Application
                             Audio
                             Files
                             Input)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Color)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Event)
           (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               Widget)
           (com.badlogic.gdx.scenes.scene2d.utils ClickListener
                                                  TextureRegionDrawable)))

(defn- call-world-fn
  [world-fn creature-properties graphics]
  (let [[f params] (->> world-fn
                        io/resource
                        slurp
                        edn/read-string)]
    ((requiring-resolve f)
     (assoc params
            :level/creature-properties (moon.world-fns.creature-tiles/prepare creature-properties
                                                                             #(graphics/texture-region graphics %))
            :textures (:graphics/textures graphics)))))

(def ^:private world-params
  {:content-grid-cell-size 16
   :world/factions-iterations {:good 15 :evil 5}
   :world/max-delta 0.04
   :world/minimum-size 0.39
   :world/z-orders [:z-order/on-ground
                    :z-order/ground
                    :z-order/flying
                    :z-order/effect]
   :world/enemy-components {:entity/fsm {:fsm :fsms/npc
                                         :initial-state :npc-sleeping}
                            :entity/faction :evil}
   :world/player-components {:creature-id :creatures/vampire
                             :components {:entity/fsm {:fsm :fsms/player
                                                       :initial-state :player-idle}
                                          :entity/faction :good
                                          :entity/player? true
                                          :entity/free-skill-points 3
                                          :entity/clickable {:type :clickable/player}
                                          :entity/click-distance-tiles 1.5}}
   :world/effect-body-props {:width 0.5
                             :height 0.5
                             :z-order :z-order/effect}
   :world/create-fns {:entity/animation             moon.animation/create
                      :entity/body                  moon.body/create
                      :entity/delete-after-duration (fn [duration {:keys [world/elapsed-time]}]
                                                      (timer/create elapsed-time duration))
                      :entity/projectile-collision  (fn create [v _world]
                                                      (assoc v :already-hit-bodies #{}))
                      :entity/stats                 moon.entity.stats/create}
   :world/after-create-fns {:entity/fsm                             (fn
                                                                      [{:keys [fsm initial-state]} eid world]
                                                                      ; fsm throws when initial-state is not part of states, so no need to assert initial-state
                                                                      ; initial state is nil, so associng it. make bug report at reduce-fsm?
                                                                      [[:tx/assoc eid :entity/fsm (assoc ((get (:world/fsms world) fsm) initial-state nil) :state initial-state)]
                                                                       [:tx/assoc eid initial-state (state/create [initial-state nil] eid world)]])
                            :entity/inventory                       moon.inventory/create!
                            :entity/skills                          moon.entity.skills/create!}

   })

(q/defrecord Context [])

(defn- open-editor!
  [{:keys [ctx/db
           ctx/graphics
           ctx/skin
           ctx/stage]}
   property-type]
  (stage/add-actor! stage
                    (overview-window/create
                     {:db db
                      :graphics graphics
                      :skin skin
                      :property-type property-type
                      :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                       (stage/add-actor! stage
                                                         (editor-window/create
                                                          {:ctx ctx
                                                           :property (db/get-raw db id)})))})))

(defn- create-info-window
  [skin stage]
  (info-window/create skin
                      {:title "Entity Info"
                       :actor-name "moon.ui.windows.entity-info"
                       :visible? false
                       :position [(ui/viewport-width stage) 0]
                       :set-label-text! (fn [{:keys [ctx/world]}]
                                          (if-let [eid (:world/mouseover-eid world)]
                                            (info/text (apply dissoc @eid [:entity/skills
                                                                           :entity/faction
                                                                           :active-skill])
                                                       world)
                                            ""))}))

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
  (proxy [Widget] []
    (draw [_batch _parent-alpha]
      (when-let [stage (actor/stage this)]
        (let [{:keys [ctx/graphics
                      ctx/world]} (stage/ctx stage)]
          (graphics/draw! graphics
                          (let [ui-mouse (:graphics/ui-mouse-position graphics)]
                            (draw-cell-rect @(:world/player-eid world)
                                            (actor/x this)
                                            (actor/y this)
                                            (let [[x y] (actor/stage->local-coordinates this ui-mouse)]
                                              (actor/hit this x y true))
                                            (actor/user-object (actor/parent this))))))))))

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
     {:skin skin
      :title title
      :actor/name "moon.ui.windows.inventory"
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

(defn- create-inventory-window
  [graphics skin stage]
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
     {:skin skin
      :title "Inventory"
      :actor/visible? false
      :position [(ui/viewport-width  stage)
                 (ui/viewport-height stage)]
      :clicked-cell-listener (fn [cell]
                               (proxy [ClickListener] []
                                 (clicked [event x y]
                                   (let [{:keys [ctx/world] :as ctx} (stage/ctx (Event/.getStage event))
                                         eid (:world/player-eid world)
                                         entity @eid
                                         state-k (:state (:entity/fsm entity))
                                         txs (state->clicked-inventory-cell [state-k (state-k entity)]
                                                                            eid
                                                                            cell)]
                                     (ctx/handle! ctx txs)))))
      :slot->texture-region slot->texture-region})))

(def state->draw-ui-view
  {:player-item-on-cursor (fn
                            [eid
                             {:keys [ctx/graphics
                                     ctx/input
                                     ctx/stage]}]
                            ; TODO see player-item-on-cursor at render layers
                            ; always draw it here at right position, then render layers does not need input/stage
                            ; can pass world to graphics, not handle here at application
                            (when (not (player-item-on-cursor/world-item? (ui/mouseover-actor stage (input/mouse-position input))))
                              [[:draw/texture-region
                                (graphics/texture-region graphics (:entity/image (:entity/item-on-cursor @eid)))
                                (:graphics/ui-mouse-position graphics)
                                {:center? true}]]))})

(def message-duration-seconds 0.5)

(defn- create-player-message-actor []
  (message/create message-duration-seconds))

(defn- player-state-handle-draws
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (let [player-eid (:world/player-eid world)
        entity @player-eid
        state-k (:state (:entity/fsm entity))]
    (when-let [f (state->draw-ui-view state-k)]
      (graphics/draw! graphics (f player-eid ctx)))))

(defn- create-player-state-draw-actor []
  (actor/create
   {:draw (fn [this _batch _parent-alpha]
            (player-state-handle-draws (stage/ctx (actor/stage this))))
    :act (fn [_ _delta])}))

(defn- create-ui-windows
  [graphics skin stage]
  (group/create
   {:actor/name "moon.ui.windows"
    :group/actors [(create-info-window skin stage)
                   (create-inventory-window graphics skin stage)]}))

(defn- create-hp-mana-bar* [create-draws]
  (actor/create
   {:act (fn [_this _delta])
    :draw (fn [actor _batch _parent-alpha]
            (when-let [stage (actor/stage actor)]
              (graphics/draw! (:ctx/graphics (stage/ctx stage))
                              (create-draws (stage/ctx stage)))))}))

(let [config {:rahmen-file "images/rahmen.png"
              :rahmenw 150
              :rahmenh 26
              :hpcontent-file "images/hp.png"
              :manacontent-file "images/mana.png"
              :y-mana 80}]
  (defn- hp-mana-bar-config
    [graphics stage]
    (let [{:keys [rahmen-file
                  rahmenw
                  rahmenh
                  hpcontent-file
                  manacontent-file
                  y-mana]} config
          [x y-mana] [(/ (ui/viewport-width stage) 2)
                      y-mana]
          rahmen-tex-reg (graphics/texture-region graphics {:image/file rahmen-file})
          y-hp (+ y-mana rahmenh)
          render-hpmana-bar (fn [x y content-file minmaxval name]
                              [[:draw/texture-region rahmen-tex-reg [x y]]
                               [:draw/texture-region
                                (graphics/texture-region graphics
                                                         {:image/file content-file
                                                          :image/bounds [0 0 (* rahmenw (val-max/ratio minmaxval)) rahmenh]})
                                [x y]]
                               [:draw/text {:text (str (utils/readable-number (minmaxval 0))
                                                       "/"
                                                       (minmaxval 1)
                                                       " "
                                                       name)
                                            :x (+ x 75)
                                            :y (+ y 2)
                                            :up? true}]])]
      (fn [{:keys [ctx/world]}]
        (let [stats (:entity/stats @(:world/player-eid world))
              x (- x (/ rahmenw 2))]
          (concat
           (render-hpmana-bar x y-hp   hpcontent-file   (stats/get-hitpoints stats) "HP")
           (render-hpmana-bar x y-mana manacontent-file (stats/get-mana      stats) "MP")))))))

(defn- create-hp-mana-bar [graphics stage]
  (create-hp-mana-bar* (hp-mana-bar-config graphics stage)))

(defn- load-sounds
  [audio files {:keys [sound-names path-format]}]
  (let [sound-name->file-handle (into {}
                                      (for [sound-name (->> sound-names io/resource slurp edn/read-string)
                                            :let [path (format path-format sound-name)]]
                                        [sound-name
                                         (Files/.internal files path)]))]
    (into {}
          (for [[sound-name file-handle] sound-name->file-handle]
            [sound-name
             (Audio/.newSound audio file-handle)]))))

(defn- create-skin [^FileHandle file-handle]
  (let [skin (Skin. file-handle)]
    (set! (.markupEnabled (-> skin (.getFont "default-font") .getData))
          true)
    skin))

(defn- create-dev-menu
  [db graphics skin]
  (let [open-editor (fn [db]
                      {:label "Editor"
                       :items (for [property-type (sort (db/property-types db))]
                                {:label (str/capitalize (name property-type))
                                 :on-click (fn [_actor ctx]
                                             (open-editor! ctx property-type))})})


        ctx-data-viewer {:label "Ctx Data"
                         :items [{:label "Show data"
                                  :on-click (fn [_actor {:keys [ctx/skin
                                                                ctx/stage] :as ctx}]
                                              (ui/show-data-viewer! stage ctx skin))}]}
        help-info-text {:label "Help"
                        :items [{:label input/info-text}]}
        select-world {:label "Select World"
                      :items (for [world-fn ["world_fns/vampire.edn"
                                             "world_fns/uf_caves.edn"
                                             "world_fns/modules.edn"]]
                               {:label (str "Start " world-fn)
                                :on-click (fn [actor {:keys [ctx/stage] :as ctx}]
                                            (let [rebuild-actors! nil
                                                  #_(fn rebuild-actors! [stage ctx]
                                                      (stage/clear! stage)
                                                      ((requiring-resolve 'moon.application.create.add-actors/step) ctx))
                                                  create-world nil
                                                  #_(requiring-resolve 'moon.application.create.world/step)
                                                  ui stage
                                                  stage (actor/stage actor)]  ; get before clear, otherwise the actor does not have a stage anymore
                                              (rebuild-actors! ui ctx)
                                              (world/dispose! (:ctx/world ctx))
                                              (stage/set-ctx! stage (create-world ctx world-fn))))})}
        update-labels [{:label "elapsed-time"
                        :update-fn (fn [ctx]
                                     (str (utils/readable-number (:world/elapsed-time (:ctx/world ctx))) " seconds"))
                        :icon "images/clock.png"}
                       {:label "FPS"
                        :update-fn (fn [ctx]
                                     (graphics/frames-per-second (:ctx/graphics ctx)))
                        :icon "images/fps.png"}
                       {:label "Mouseover-entity id"
                        :update-fn (fn [{:keys [ctx/world]}]
                                     (let [eid (:world/mouseover-eid world)]
                                       (when-let [entity (and eid @eid)]
                                         (:entity/id entity))))
                        :icon "images/mouseover.png"}
                       {:label "paused?"
                        :update-fn (comp :world/paused? :ctx/world)}
                       {:label "GUI"
                        :update-fn (fn [{:keys [ctx/graphics]}]
                                     (mapv int (:graphics/ui-mouse-position graphics)))}
                       {:label "World"
                        :update-fn (fn [{:keys [ctx/graphics]}]
                                     (mapv int (:graphics/world-mouse-position graphics)))}
                       {:label "Zoom"
                        :update-fn (fn [ctx]
                                     (graphics/zoom (:ctx/graphics ctx)))
                        :icon "images/zoom.png"}]]
    (dev-menu/create
     {:menus [ctx-data-viewer
              (open-editor db)
              help-info-text
              select-world]
      :update-labels (for [item update-labels]
                       (if (:icon item)
                         (update item :icon #(get (:graphics/textures graphics) %))
                         item))
      :skin skin})))

(defn do!
  [^Application app config]
  (let [db (db/create)
        graphics (graphics/create! (.getGraphics app) (.getFiles app) (:graphics config)) ; graphics/sounds/input already part of application?!
        stage (ui/create! graphics)
        skin (create-skin (.internal (.getFiles app) "uiskin.json"))
        ctx (merge (map->Context {})
                   {:ctx/audio (load-sounds (.getAudio app) (.getFiles app) (:audio config))
                    :ctx/db db
                    :ctx/graphics graphics
                    :ctx/input (.getInput app)
                    :ctx/stage stage
                    :ctx/skin skin})]
    (Input/.setInputProcessor (.getInput app) stage)
    ; all ui building inside moon.ui ??
    ; just pass game-fns ?
    (doseq [actor [(create-dev-menu db graphics skin)
                   (action-bar/create)
                   (create-hp-mana-bar graphics stage)
                   (create-ui-windows graphics skin stage)
                   (create-player-state-draw-actor)
                   (create-player-message-actor)]]
      (stage/add-actor! stage actor))
    (let [world-fn-result (call-world-fn (:world config)
                                         (db/all-raw db :properties/creatures)
                                         graphics)
          world (world/create world-params world-fn-result)
          ctx (assoc ctx :ctx/world world)
          _ (ctx/handle! ctx
                         [[:tx/spawn-creature (let [{:keys [creature-id
                                                            components]} (:world/player-components world)]
                                                {:position (mapv (partial + 0.5) (:world/start-position world))
                                                 :creature-property (db/build db creature-id)
                                                 :components components})]])
          ctx (let [eid (get @(:world/entity-ids world) 1)]
                (assert (:entity/player? @eid))
                (assoc-in ctx [:ctx/world :world/player-eid] eid))]
      (ctx/handle!
       ctx
       (for [[position creature-id] (tiled-map/spawn-positions (:world/tiled-map world))]
         [:tx/spawn-creature {:position (mapv (partial + 0.5) position)
                              :creature-property (db/build db (keyword creature-id))
                              :components (:world/enemy-components world)}]))

      ctx)))
