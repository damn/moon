(ns application.create
  (:require [clojure.core-ext :refer [actions!
                                      define-order
                                      edn-resource
                                      reduce-actions!
                                      safe-merge
                                      ]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.math.vector2 :as v]
            [clojure.string :as str]
            [game.ctx :as ctx]
            [game.effect :as effect]
            [game.entity :as entity]
            [game.info :as info]
            [game.schema]
            [game.state :as state]
            [gdx.application :as app]
            [gdx.audio.sound :as sound]
            [gdx.files :as files]
            [gdx.graphics :as graphics]
            [gdx.graphics.color :as color]
            [gdx.graphics.colors :as colors]
            [gdx.graphics.orthographic-camera :as camera]
            [gdx.graphics.shape-drawer :as shape-drawer]
            [gdx.input :as input]
            [gdx.input.buttons :as input.buttons]
            [gdx.input.keys :as input.keys]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.event :as event]
            [gdx.scenes.scene2d.group :as group]
            [gdx.scenes.scene2d.stage :as stage]
            [gdx.scenes.scene2d.ui.action-bar :as action-bar]
            [gdx.scenes.scene2d.ui.data-viewer-window :as data-viewer-window]
            [gdx.scenes.scene2d.ui.dev-menu :as dev-menu]
            [gdx.scenes.scene2d.ui.image :as image]
            [gdx.scenes.scene2d.ui.info-window :as info-window]
            [gdx.scenes.scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.stack :as stack]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]
            [gdx.scenes.scene2d.ui.widget :as widget]
            [gdx.scenes.scene2d.ui.window :as window]
            [gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [gdx.textures]
            [gdx.utils.viewport.fit-viewport :as fit-viewport]
            [moon.body :as body]
            [moon.content-grid :as content-grid]
            [moon.creature-tiles]
            [moon.db :as db]
            [moon.grid :as grid]
            [moon.grid2d :as g2d]
            [moon.inventory :as inventory]
            [moon.number :as number]
            [moon.raycaster :as raycaster]
            [moon.stats :as stats]
            [moon.textures :as textures]
            [moon.tiled-map :as tiled-map]
            [moon.timer :as timer]
            [moon.ui.error-window]
            [moon.ui.inventory-window :as inventory-window]
            [moon.ui.property-overview-window]
            [moon.val-max :as val-max]
            [qrecord.core :as q]
            [reduce-fsm :as fsm])
  (:import (com.badlogic.gdx.graphics Pixmap
                                      Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)))


(def world-fn-file
   "world_fns/modules.edn"
  ; "world_fns/vampire.edn"
  ; "world_fns/uf_caves.edn"
  )

(q/defrecord Entity [entity/body])

(def txs-fn-map
  {
   :tx/state-exit               (fn [ctx eid [state-k state-v]]
                                  (state/exit [state-k state-v] eid ctx))
   :tx/audiovisual              (fn
                                  [{:keys [ctx/db]} position audiovisual]
                                  (let [{:keys [tx/sound
                                                entity/animation]} (if (keyword? audiovisual)
                                                                     (db/build db audiovisual)
                                                                     audiovisual)]
                                    [[:tx/sound sound]
                                     [:tx/spawn-effect
                                      position
                                      {:entity/animation (assoc animation :delete-after-stopped? true)}]]))
   :tx/assoc                    (fn [_ctx eid k value]
                                  (swap! eid assoc k value)
                                  nil)
   :tx/assoc-in                 (fn [_ctx eid ks value]
                                  (swap! eid assoc-in ks value)
                                  nil)
   :tx/dissoc                   (fn [_ctx eid k]
                                  (swap! eid dissoc k)
                                  nil)
   :tx/update                   (fn [_ctx eid & params]
                                  (apply swap! eid update params)
                                  nil)
   :tx/mark-destroyed           (fn [_ctx eid]
                                  (swap! eid assoc :entity/destroyed? true)
                                  nil)
   :tx/set-cooldown             (fn [{:keys [ctx/elapsed-time]} eid skill]
                                  (swap! eid assoc-in [:entity/skills
                                                       (:property/id skill)
                                                       :skill/cooling-down?]
                                         (timer/create elapsed-time (:skill/cooldown skill)))
                                  nil)
   :tx/add-text-effect          (fn [{:keys [ctx/elapsed-time]} eid text duration]
                                  [[:tx/assoc
                                    eid
                                    :entity/string-effect
                                    (if-let [existing (:entity/string-effect @eid)]
                                      (-> existing
                                          (update :text str "\n" text)
                                          (update :counter timer/increment duration))
                                      {:text text
                                       :counter (timer/create elapsed-time duration)})]])
   :tx/add-skill                (fn [_ctx eid {:keys [property/id] :as skill}]
                                  {:pre [(not (contains? (:entity/skills @eid) id))]}
                                  (swap! eid update :entity/skills assoc id skill)
                                  nil)
   :tx/set-item                 (fn [_ctx eid cell item]
                                  (let [entity @eid
                                        inventory (:entity/inventory entity)]
                                    (assert (and (nil? (get-in inventory cell))
                                                 (inventory/valid-slot? cell item)))
                                    (swap! eid assoc-in (cons :entity/inventory cell) item)
                                    (when (inventory/applies-modifiers? cell)
                                      (swap! eid update :entity/stats stats/add (:stats/modifiers item)))
                                    nil))
   :tx/remove-item              (fn [_ctx eid cell]
                                  (let [entity @eid
                                        item (get-in (:entity/inventory entity) cell)]
                                    (assert item)
                                    (swap! eid assoc-in (cons :entity/inventory cell) nil)
                                    (when (inventory/applies-modifiers? cell)
                                      (swap! eid update :entity/stats stats/remove-mods (:stats/modifiers item)))
                                    nil))
   :tx/pickup-item              (fn [_ctx eid item]
                                  (inventory/assert-valid-item? item)
                                  (let [[cell cell-item] (inventory/can-pickup-item? (:entity/inventory @eid) item)]
                                    (assert cell)
                                    (assert (or (inventory/stackable? item cell-item)
                                                (nil? cell-item)))
                                    (if (inventory/stackable? item cell-item)
                                      (do
                                       #_(tx/stack-item ctx eid cell item))
                                      [[:tx/set-item eid cell item]])))
   :tx/event                    (fn send-event!
                                  ([ctx eid event]
                                   (send-event! ctx eid event nil))
                                  ([ctx eid event params]
                                   (let [fsm (:entity/fsm @eid)
                                         _ (assert fsm)
                                         old-state-k (:state fsm)
                                         new-fsm (fsm/fsm-event fsm event)
                                         new-state-k (:state new-fsm)]
                                     (when-not (= old-state-k new-state-k)
                                       (let [old-state-obj (let [k (:state (:entity/fsm @eid))]
                                                             [k (k @eid)])
                                             new-state-obj [new-state-k (state/create [new-state-k params] eid ctx)]]
                                         [[:tx/assoc       eid :entity/fsm new-fsm]
                                          [:tx/assoc       eid new-state-k (new-state-obj 1)]
                                          [:tx/dissoc      eid old-state-k]
                                          [:tx/state-exit  eid old-state-obj]
                                          [:tx/state-enter eid new-state-obj]])))))
   :tx/register-eid             (fn [ctx eid]
                                  (assert (and (not (contains? @eid :entity/id))))
                                  (let [id (swap! (:ctx/id-counter ctx) inc)]
                                    (assert (number? id))
                                    (swap! eid assoc :entity/id id)
                                    (swap! (:ctx/entity-ids ctx) assoc id eid))

                                  (assert (:entity/body @eid)) ; -< inside content grid
                                  (content-grid/add-entity! (:ctx/content-grid ctx) eid)

                                  (assert (:entity/body @eid)) ; <- inside the grid add fn ?
                                  (when (:body/collides? (:entity/body @eid))
                                    (assert (grid/valid-position? (:ctx/grid ctx) (:entity/body @eid) (:entity/id @eid))))
                                  (grid/set-touched-cells! (:ctx/grid ctx) eid)
                                  (when (:body/collides? (:entity/body @eid)) ; entity/collides? separate fooziboosh, no 'when' just a callback?
                                    (grid/set-occupied-cells! (:ctx/grid ctx) eid))

                                  nil
                                  ; TODO what should a tx return? nil? ctx?
                                  )
   :tx/unregister-eid           (fn
                                  [{:keys [ctx/content-grid
                                           ctx/entity-ids
                                           ctx/grid]
                                    :as ctx}
                                   eid]
                                  (let [id (:entity/id @eid)]
                                    (assert (contains? @entity-ids id))
                                    (swap! entity-ids dissoc id))
                                  (content-grid/remove-entity! content-grid eid)
                                  (grid/remove-from-touched-cells! grid eid)
                                  (when (:body/collides? (:entity/body @eid))
                                    (grid/remove-from-occupied-cells! grid eid))
                                  nil)
   :tx/state-enter              (fn [_ctx eid [state-k state-v]]
                                  (state/enter [state-k state-v] eid))
   :tx/effect                   (fn [ctx effect-ctx effects]
                                  (mapcat #(effect/handle % effect-ctx ctx)
                                          (filter #(effect/applicable? % effect-ctx) effects)))
   :tx/spawn-alert              (fn
                                  [{:keys [ctx/elapsed-time]} position faction duration]
                                  [[:tx/spawn-effect
                                    position
                                    {:entity/alert-friendlies-after-duration
                                     {:counter (timer/create elapsed-time duration)
                                      :faction faction}}]])
   :tx/spawn-line               (fn
                                  [_ctx {:keys [start end duration color thick?]}]
                                  [[:tx/spawn-effect
                                    start
                                    {:entity/line-render {:thick? thick? :end end :color color}
                                     :entity/delete-after-duration duration}]])
   :tx/move-entity              (fn
                                  [{:keys [ctx/content-grid
                                           ctx/grid]}
                                   eid]
                                  (content-grid/position-changed! content-grid eid)
                                  (grid/remove-from-touched-cells! grid eid)
                                  (grid/set-touched-cells! grid eid)
                                  (when (:body/collides? (:entity/body @eid))
                                    (grid/remove-from-occupied-cells! grid eid)
                                    (grid/set-occupied-cells! grid eid))
                                  nil)
   :tx/spawn-projectile         (fn
                                  [_ctx
                                   {:keys [position direction faction]}
                                   {:keys [entity/image
                                           projectile/max-range
                                           projectile/speed
                                           entity-effects
                                           projectile/size
                                           projectile/piercing?] :as projectile}]
                                  [[:tx/spawn-entity
                                    {:entity/body {:position position
                                                   :width size
                                                   :height size
                                                   :z-order :z-order/flying
                                                   :rotation-angle (v/angle-from-vector direction)}
                                     :entity/movement {:direction direction
                                                       :speed speed}
                                     :entity/image image
                                     :entity/faction faction
                                     :entity/delete-after-duration (/ max-range speed)
                                     :entity/destroy-audiovisual :audiovisuals/hit-wall
                                     :entity/projectile-collision {:entity-effects entity-effects
                                                                   :piercing? piercing?}}]])
   :tx/spawn-effect             (fn [_ctx position components]
                                  [[:tx/spawn-entity
                                    (assoc components
                                           :entity/body {:width 0.5
                                                         :height 0.5
                                                         :z-order :z-order/effect
                                                         :position position})]])
   :tx/spawn-item               (fn [_ctx position item]
                                  [[:tx/spawn-entity
                                    {:entity/body {:position position
                                                   :width 0.75
                                                   :height 0.75
                                                   :z-order :z-order/on-ground}
                                     :entity/image (:entity/image item)
                                     :entity/item item
                                     :entity/clickable {:type :clickable/item
                                                        :text (:property/pretty-name item)}}]])
   :tx/spawn-creature           (fn
                                  [_ctx
                                   {:keys [position
                                           creature-property
                                           components]}]
                                  (assert creature-property)
                                  [[:tx/spawn-entity
                                    (-> creature-property
                                        (assoc :entity/body (let [{:keys [body/width body/height #_body/flying?]} (:entity/body creature-property)]
                                                              {:position position
                                                               :width  width
                                                               :height height
                                                               :collides? true
                                                               :z-order :z-order/ground #_(if flying? :z-order/flying :z-order/ground)}))
                                        (assoc :entity/destroy-audiovisual :audiovisuals/creature-die)
                                        (safe-merge components))]])
   :tx/spawn-entity             (fn [ctx entity]
                                  (let [entity (reduce (fn [m [k v]]
                                                         (assoc m k (entity/create [k v] ctx)))
                                                       {}
                                                       entity)
                                        entity (merge (map->Entity {}) entity)
                                        eid (atom entity)]
                                    (cons
                                     [:tx/register-eid eid]
                                     (mapcat #(entity/after-create % eid ctx) @eid))))
   :tx/sound                    (fn [& params] nil)
   :tx/toggle-inventory-visible (fn [& params] nil)
   :tx/show-message             (fn [& params] nil)
   :tx/show-modal               (fn [& params] nil)
   }
  )

(def reaction-txs-fn-map
  {
   :tx/sound                    (fn
                                  [{:keys [ctx/audio] :as ctx} sound-name]
                                  (let [sounds audio]
                                    (assert (contains? sounds sound-name) (str sound-name))
                                    (sound/play! (get sounds sound-name)))
                                  ctx)
   :tx/toggle-inventory-visible (fn
                                  [{:keys [ctx/stage] :as ctx}]
                                  (-> stage
                                      (stage/find-actor "moon.ui.windows.inventory")
                                      actor/toggle-visible!)
                                  ctx)
   :tx/show-message             (fn
                                  [{:keys [ctx/stage] :as ctx} message]
                                  (-> stage
                                      (stage/find-actor "player-message")
                                      (actor/set-user-object! (atom {:text message
                                                                     :counter 0})))
                                  ctx)
   :tx/show-modal               (fn
                                  [{:keys [ctx/skin
                                           ctx/stage]
                                    :as ctx}
                                   {:keys [title text button-text on-click]}]
                                  (assert (not (stage/find-actor stage "moon.ui.modal-window")))
                                  (stage/add-actor! stage
                                                    (window/create
                                                     {:title title
                                                      :skin skin
                                                      :window/modal? true
                                                      :table/rows [[{:actor (label/create
                                                                             {:text text
                                                                              :skin skin})}]
                                                                   [{:actor (text-button/create
                                                                             {:text button-text
                                                                              :skin skin
                                                                              :actor/listeners {:listener/change (fn [_event _actor]
                                                                                                                   (actor/remove! (stage/find-actor stage "moon.ui.modal-window"))
                                                                                                                   (on-click))}})}]]
                                                      :actor/name "moon.ui.modal-window"
                                                      :actor/position [(/ (:viewport/world-width (:stage/viewport stage)) 2)
                                                                       (* (:viewport/world-height (:stage/viewport stage)) (/ 3 4))
                                                                       :align/center]}))
                                  ctx)
   :tx/set-item                 (fn
                                  [{:keys [ctx/skin
                                           ctx/stage
                                           ctx/textures]
                                    :as ctx}
                                   eid cell item]
                                  (when (:entity/player? @eid)
                                    (-> stage
                                        ;(group/find-actor "moon.ui.windows")
                                        (stage/find-actor "moon.ui.windows.inventory")
                                        (inventory-window/set-item! cell {:texture-region (textures/texture-region textures (:entity/image item))
                                                                          :tooltip-text (info/text item ctx)}
                                                                    skin)))
                                  ctx)
   :tx/remove-item              (fn
                                  [{:keys [ctx/stage] :as ctx} eid cell]
                                  (when (:entity/player? @eid)
                                    (-> stage
                                        ;(group/find-actor "moon.ui.windows")
                                        (stage/find-actor "moon.ui.windows.inventory")
                                        (inventory-window/remove-item! cell)))
                                  ctx)
   :tx/add-skill                (fn
                                  [{:keys [ctx/skin
                                           ctx/stage
                                           ctx/textures]
                                    :as ctx}
                                   eid skill]
                                  (when (:entity/player? @eid)
                                    (-> stage
                                        (stage/find-actor "moon.ui.action-bar")
                                        (action-bar/add-skill! {:skill-id (:property/id skill)
                                                                :texture-region (textures/texture-region textures (:entity/image skill))
                                                                :tooltip-text (info/text skill ctx)}
                                                               skin)))
                                  ctx)
   }
  )



(q/defrecord Context []
  ctx/Context
  (key-pressed?
    [{:keys [ctx/app]} input-key]
    (input/key-pressed? (app/input app) input-key))

  (world-unit-scale [ctx]
    (:ctx/world-unit-scale ctx))

  (mouse-position [{:keys [ctx/app]}]
    (input/mouse-position (app/input app)))

  (button-just-pressed? [{:keys [ctx/app]} input-button]
    (input/button-just-pressed? (app/input app) input-button))

  ; It is possible to put items out of sight, losing them.
  ; Because line of sight checks center of entity only, not corners
  ; this is okay, you have thrown the item over a hill, thats possible.
  (item-place-position [{:keys [ctx/world-mouse-position]} player-entity]
    (let [player-position (:body/position (:entity/body player-entity))
          ; so you cannot put it out of your own reach
          maxrange (- (:entity/click-distance-tiles player-entity) 0.1)]
      (v/add player-position
             (v/scale (v/direction player-position world-mouse-position)
                      (min maxrange
                           (v/distance player-position world-mouse-position))))))

  (sound-names [{:keys [ctx/audio]}]
    (map first audio))

  (key-just-pressed? [{:keys [ctx/app]} input-key]
    (input/key-just-pressed? (app/input app) input-key))

  (do! [ctx txs]
    (let [handled-txs (try (actions! txs-fn-map ctx txs)
                           (catch Throwable t
                             (throw (ex-info "Error handling txs"
                                             {:txs txs} t))))]
      (reduce-actions! reaction-txs-fn-map
                       ctx
                       handled-txs)))

  (player-movement-vector [ctx]
    (let [r (when (ctx/key-pressed? ctx input.keys/d) [1  0])
          l (when (ctx/key-pressed? ctx input.keys/a) [-1 0])
          u (when (ctx/key-pressed? ctx input.keys/w) [0  1])
          d (when (ctx/key-pressed? ctx input.keys/s) [0 -1])]
      (when (or r l u d)
        (let [v (v/normalise (reduce v/add [0 0] (remove nil? [r l u d])))]
          (when (pos? (v/length v))
            v)))))
  )

(defn create-record [ctx]
  (merge (map->Context {}) ctx))


(defn frames-per-second
  [{:keys [ctx/app]}]
  (graphics/frames-per-second (app/graphics app)))






(defn camera-zoom [{:keys [ctx/world-viewport]}]
  (camera/zoom (:viewport/camera world-viewport)))









(defn create-app [app]
  (tooltip-manager/set-initial-time! 0)
  (colors/put! {"PRETTY_NAME" [0.84 0.8 0.52 1]})
  (let [batch (com.badlogic.gdx.graphics.g2d.SpriteBatch.)
        white-pixel-texture (graphics/white-pixel-texture)
        world-unit-scale (float (/ 48))]
    {:ctx/app app
     :ctx/audio (into {}
                      (for [sound-name (-> "sounds.edn" io/resource slurp edn/read-string)]
                        [sound-name
                         (.newSound (app/audio app)
                                    (files/internal (app/files app) (format "sounds/%s.wav" sound-name)))]))
     :ctx/batch batch
     :ctx/shape-drawer-texture white-pixel-texture
     :ctx/shape-drawer (shape-drawer/create batch (com.badlogic.gdx.graphics.g2d.TextureRegion. white-pixel-texture 1 0 1 1))
     :ctx/default-font (let [path "exocet/films.EXL_____.ttf"
                             size 16
                             quality-scaling 2
                             generator (FreeTypeFontGenerator. (files/internal (app/files app) path))
                             font (.generateFont generator
                                                 (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                                                   (set! (.size params) (* size quality-scaling))
                                                   ; texture.filter/linear because scaling to world-units
                                                   (set! (.minFilter params) Texture$TextureFilter/Linear)
                                                   (set! (.magFilter params) Texture$TextureFilter/Linear)
                                                   params))]
                         (.dispose generator)
                         (.setScale (.getData font) (/ quality-scaling))
                         (set! (.markupEnabled (.getData font)) true)
                         (.setUseIntegerPositions font false)
                         font)
     :ctx/world-unit-scale world-unit-scale
     :ctx/world-viewport (let [world-width  (* 1440 world-unit-scale)
                               world-height (* 900  world-unit-scale)]
                           (fit-viewport/create world-width
                                                world-height
                                                (camera/create
                                                 {:y-down? false
                                                  :world-width world-width
                                                  :world-height world-height})))
     :ctx/cursors (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
                    (update-vals data
                                 (fn [[path [hotspot-x hotspot-y]]]
                                   (let [pixmap (Pixmap. (files/internal (app/files app) (format path-format path)))
                                         cursor (graphics/new-cursor (app/graphics app) pixmap hotspot-x hotspot-y)]
                                     (.dispose pixmap)
                                     cursor))))
     :ctx/stage (let [stage (stage/create (fit-viewport/create 1440 900) batch)]
                  (input/set-processor! (app/input app) stage)
                  stage)
     :ctx/skin (let [skin (Skin. (files/internal (app/files app) "uiskin.json"))]
                 (set! (.markupEnabled (-> skin
                                           (.getFont "default-font")
                                           .getData))
                       true)
                 skin)
     :ctx/unit-scale (atom 1)}))

(defn create-textures
  [ctx]
  (assoc ctx :ctx/textures (gdx.textures/create (app/files (:ctx/app ctx)))))

(defn unorganised [ctx]
  (assoc ctx

         ;frame
         :ctx/active-entities nil
         :ctx/delta-time nil
         :ctx/ui-mouse-position nil
         :ctx/world-mouse-position nil
         :ctx/mouseover-eid nil
         :ctx/paused? false

         ; stuff
         :ctx/elapsed-time 0
         :ctx/potential-field-cache (atom nil)
         :ctx/id-counter (atom 0)
         :ctx/entity-ids (atom {})

         ; constants (config & not state?)
         :ctx/factions-iterations {:good 15 :evil 5}
         :ctx/max-delta 0.04
         :ctx/minimum-size 0.39
         :ctx/z-orders [:z-order/on-ground
                        :z-order/ground
                        :z-order/flying
                        :z-order/effect]
         ))

(defn create-controls [ctx]
  (assoc ctx
         :ctx/controls {
                        :zoom-in input.keys/minus
                        :zoom-out input.keys/equals
                        :unpause-once input.keys/p
                        :unpause-continously input.keys/space
                        :close-windows-key input.keys/escape
                        :toggle-inventory  input.keys/i
                        :toggle-entity-info input.keys/e
                        :open-debug-button input.buttons/right
                        }
         :ctx/controls-info (str/join "\n"
                                      ["[W][A][S][D] - Move"
                                       "[ESCAPE] - Close windows"
                                       "[I] - Inventory window"
                                       "[E] - Entity Info window"
                                       "[-]/[=] - Zoom"
                                       "[P]/[SPACE] - Unpause"
                                       "rightclick on tile or entity - open debug data window"
                                       "Leftmouse click - use skill/drop item on cursor"])))

(def black [0 0 0 1])
(def white [1 1 1 1])
(def gray  [0.5 0.5 0.5 1])
(def red   [1 0 0 1])

(def outline-alpha 0.4)

(defn create-colors [ctx]
  (assoc ctx :ctx/colors
         {
          :colors/mouseover-tile-air  (color/float-bits [1 1 0 0.5])
          :colors/mouseover-tile-none (color/float-bits [1 0 0 0.5])
          :colors/debug-body-outline-collides (color/float-bits white)
          :colors/debug-body-outline (color/float-bits gray)
          :colors/debug-body-outline-render-error (color/float-bits red)
          :colors/debug-cell-entities (color/float-bits [1 0 0 0.6])
          :colors/debug-cell-occupied (color/float-bits [0 0 1 0.6])
          :colors/debug-potential-field (fn [ratio]
                                          (color/float-bits [ratio (- 1 ratio) ratio 0.6]))
          :colors/target-all-line (color/float-bits [1 0 0 0.75])
          :colors/target-all-render (color/float-bits [1 0 0 0.5])
          :colors/target-entity-line (color/float-bits [1 0 0 0.75])
          :colors/target-entity-in-range (color/float-bits [1 0 0 0.5])
          :colors/target-entity-not-in-range (color/float-bits [1 1 0 0.5])
          :colors/enemy-color (color/float-bits [1 0 0 outline-alpha])
          :colors/friendly-color (color/float-bits [0 1 0 outline-alpha])
          :colors/neutral-color  (color/float-bits [1 1 1 outline-alpha])
          :colors/hp-bar (fn [ratio]
                           (let [ratio (float ratio)
                                 color (cond
                                        (> ratio 0.75) :green
                                        (> ratio 0.5)  :darkgreen
                                        (> ratio 0.25) :yellow
                                        :else          :red)]
                             (color {:green     (color/float-bits [0 0.8 0 1])
                                     :darkgreen (color/float-bits [0 0.5 0 1])
                                     :yellow    (color/float-bits [0.5 0.5 0 1])
                                     :red       (color/float-bits [0.5 0 0 1])})))
          :colors/hp-bar-rect (color/float-bits black)
          :colors/temp-modifier (color/float-bits [0.5 0.5 0.5 0.4])
          :colors/active-skill-circle (color/float-bits [1 1 1 0.125])
          :colors/active-skill-sector (color/float-bits [1 1 1 0.5])
          :colors/stunned (color/float-bits [1 1 1 0.6])
          :colors/explored-tile (color/float-bits [0.5 0.5 0.5 1])
          :colors/visible-tile (color/float-bits [1 1 1 1])
          :colors/invisible-tile (color/float-bits [0 0 0 1])
          :colors/droppable-item (color/float-bits [0 0.6 0 0.8 1])
          :colors/not-allowed-drop-item (color/float-bits [0.6 0 0 0.8 1])
          :colors/item-rect (color/float-bits [0.5 0.5 0.5 1])
          }))

(defn create-render-z-order
  [{:keys [ctx/z-orders]
    :as ctx}]
  (assoc ctx :ctx/render-z-order (define-order z-orders)))

(defn create-max-speed
  [{:keys [ctx/minimum-size
           ctx/max-delta]
    :as ctx}]
  (assoc ctx :ctx/max-speed (/ minimum-size max-delta)))

(defn create-db [ctx]
  (assoc ctx :ctx/db (db/create)))

(defn create-tiled-map
  [{:keys [ctx/db
           ctx/textures]
    :as ctx}]
  (let [[f params] (edn-resource world-fn-file)
        {:keys [tiled-map
                start-position]} (f
                                  (assoc params
                                         :level/creature-properties (moon.creature-tiles/prepare
                                                                     (db/all-raw db :properties/creatures)
                                                                     #(textures/texture-region textures %))
                                         :textures textures))]
    (assoc ctx
           :ctx/tiled-map tiled-map
           :ctx/start-position start-position)))

(defn create-dev-menu
  [{:keys [ctx/controls-info
           ctx/db
           ctx/skin
           ctx/textures]}]
  (dev-menu/create
   {:menus [
            {:label "Ctx Data"
             :items [{:label "Show data"
                      :on-click (fn [_actor {:keys [ctx/skin
                                                    ctx/stage] :as ctx}]
                                  (stage/add-actor! stage
                                                    (data-viewer-window/create
                                                     {:title "Data View"
                                                      :data ctx
                                                      :width 1000
                                                      :height 1000
                                                      :skin skin})))}]}
            {:label "Editor"
             :items (for [property-type (sort (db/property-types db))]
                      {:label (str/capitalize (name property-type))
                       :on-click (fn [_actor {:keys [ctx/db
                                                     ctx/skin
                                                     ctx/stage
                                                     ctx/textures]
                                              :as ctx}]
                                   (stage/add-actor! stage
                                                     (moon.ui.property-overview-window/create
                                                      {:db db
                                                       :textures textures
                                                       :skin skin
                                                       :property-type property-type
                                                       :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                                                        (stage/add-actor! stage
                                                                                          (game.schema/property-editor-window
                                                                                           {:ctx ctx
                                                                                            :property (db/get-raw db id)})))})))})}
            {:label "Help"
             :items [{:label controls-info}]}
            {:label "Select World"
             :items (for [world-fn ["world_fns/vampire.edn"
                                    "world_fns/uf_caves.edn"
                                    "world_fns/modules.edn"]]
                      {:label (str "Start " world-fn)
                       :on-click (fn [actor {:keys [ctx/stage] :as ctx}]
                                   #_(let [rebuild-actors! nil
                                           #_(fn rebuild-actors! [stage ctx]
                                               (.clear stage)
                                               ((requiring-resolve 'game.create.add-actors/step) ctx))
                                           create-world nil
                                           #_(requiring-resolve 'game.create.world/step)
                                           ui stage
                                           stage (Actor/.getStage actor)]  ; get before clear, otherwise the actor does not have a stage anymore
                                       (rebuild-actors! ui ctx)
                                       #_(disposable/dispose! (:ctx/tiled-map ctx))
                                       (set! (.ctx ^Stage stage) (create-world ctx world-fn))))})}

            ]
    :update-labels (for [item [
                               {:label "elapsed-time"
                                :update-fn (fn [{:keys [ctx/elapsed-time]}]
                                             (str (number/readable elapsed-time) " seconds"))
                                :icon "images/clock.png"}
                               {:label "FPS"
                                :update-fn frames-per-second
                                :icon "images/fps.png"}
                               {:label "Mouseover-entity id"
                                :update-fn (fn [{:keys [ctx/mouseover-eid]}]
                                             (when-let [entity (and mouseover-eid @mouseover-eid)]
                                               (:entity/id entity)))
                                :icon "images/mouseover.png"}
                               {:label "paused?"
                                :update-fn :ctx/paused?}
                               {:label "GUI"
                                :update-fn (fn [{:keys [ctx/ui-mouse-position]}]
                                             (mapv int ui-mouse-position))}
                               {:label "World"
                                :update-fn (fn [{:keys [ctx/world-mouse-position]}]
                                             (mapv int world-mouse-position))}
                               {:label "Zoom"
                                :update-fn camera-zoom
                                :icon "images/zoom.png"}
                               ]]
                     (if (:icon item)
                       (update item :icon #(get textures %))
                       item))
    :skin skin}))

(defn create-hp-mana-bar
  [{:keys [ctx/textures
           ctx/stage]}]
  (let [{:keys [rahmen-file
                rahmenw
                rahmenh
                hpcontent-file
                manacontent-file
                y-mana]} {:rahmen-file "images/rahmen.png"
                          :rahmenw 150
                          :rahmenh 26
                          :hpcontent-file "images/hp.png"
                          :manacontent-file "images/mana.png"
                          :y-mana 80}
        [x y-mana] [(/ (:viewport/world-width (:stage/viewport stage)) 2)
                    y-mana]
        rahmen-tex-reg (textures/texture-region textures {:image/file rahmen-file})
        y-hp (+ y-mana rahmenh)
        render-hpmana-bar (fn [x y content-file minmaxval name]
                            [[:draw/texture-region rahmen-tex-reg [x y]]
                             [:draw/texture-region
                              (textures/texture-region textures
                                                       {:image/file content-file
                                                        :image/bounds [0 0 (* rahmenw (val-max/ratio minmaxval)) rahmenh]})
                              [x y]]
                             [:draw/text {:text (str (number/readable (minmaxval 0))
                                                     "/"
                                                     (minmaxval 1)
                                                     " "
                                                     name)
                                          :x (+ x 75)
                                          :y (+ y 2)
                                          :up? true}]])
        create-draws (fn [{:keys [ctx/player-eid]}]
                       (let [stats (:entity/stats @player-eid)
                             x (- x (/ rahmenw 2))]
                         (concat
                          (render-hpmana-bar x y-hp   hpcontent-file   (stats/get-hitpoints stats) "HP")
                          (render-hpmana-bar x y-mana manacontent-file (stats/get-mana      stats) "MP"))))]
    (actor/create
     {:draw! (fn [this _batch _parent-alpha]
               (when-let [stage (actor/stage this)]
                 (ctx/draw! (:stage/ctx stage)
                            (create-draws (:stage/ctx stage)))))})))

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

(defn spawn-player
  [{:keys [ctx/db
           ctx/entity-ids
           ctx/start-position]
    :as ctx}]
  (ctx/do! ctx
           [[:tx/spawn-creature {:position (mapv (partial + 0.5) start-position)
                                 :creature-property (db/build db :creatures/vampire)
                                 :components {:entity/fsm {:fsm :fsms/player
                                                           :initial-state :player-idle}
                                              :entity/faction :good
                                              :entity/player? true
                                              :entity/free-skill-points 3
                                              :entity/clickable {:type :clickable/player}
                                              :entity/click-distance-tiles 1.5}}]])
  (let [eid (get @entity-ids 1)]
    (assert (:entity/player? @eid))
    (assoc ctx :ctx/player-eid eid)))

(defn spawn-enemies
  [{:keys [ctx/db
           ctx/tiled-map]
    :as ctx}]
  (ctx/do!
   ctx
   (for [[position creature-id] (tiled-map/spawn-positions tiled-map)]
     [:tx/spawn-creature {:position (mapv (partial + 0.5) position)
                          :creature-property (db/build db (keyword creature-id))
                          :components {:entity/fsm {:fsm :fsms/npc
                                                    :initial-state :npc-sleeping}
                                       :entity/faction :evil}}]))
  ctx)

(defn add-stage-actors
  [{:keys [ctx/stage]
    :as ctx}]
  (doseq [actor [(create-dev-menu ctx)
                 (action-bar/create)
                 (create-hp-mana-bar ctx)
                 (group/create
                  {:group/actors [(create-info-window ctx)
                                  (create-inventory-window ctx)]
                   :actor/name "moon.ui.windows"})
                 (create-player-state-draw)
                 (create-player-message-actor)]]
    (stage/add-actor! stage actor))
  ctx)

(defn create-grid
  [{:keys [ctx/tiled-map]
    :as ctx}]
  (assoc ctx :ctx/grid (grid/create tiled-map)))

(defn create-content-grid
  [{:keys [ctx/tiled-map]
    :as ctx}]
  (assoc ctx :ctx/content-grid (content-grid/create (.get (.getProperties tiled-map) "width")
                                                    (.get (.getProperties tiled-map) "height")
                                                    16)))

(defn create-explored-tile-corners
  [{:keys [ctx/tiled-map]
    :as ctx}]
  (assoc ctx :ctx/explored-tile-corners
         (atom (g2d/create-grid (.get (.getProperties tiled-map) "width")
                                (.get (.getProperties tiled-map) "height")
                                (constantly false)))))

(defn create-raycaster
  [{:keys [ctx/grid] :as ctx}]
  (assoc ctx :ctx/raycaster (raycaster/create grid)))

(defn do! [app]
  (-> app
      create-app
      create-textures
      create-record
      unorganised
      create-controls
      create-colors
      create-render-z-order
      create-max-speed
      create-db
      add-stage-actors
      create-tiled-map
      create-grid
      create-content-grid
      create-explored-tile-corners
      create-raycaster
      spawn-player
      spawn-enemies))









