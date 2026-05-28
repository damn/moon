; Remove:
; * bind-root [x]
; * protocols
; * extend-type
; * (multimethods)   - just function maps ? - or actual protocols ? @ create types ... ?
(ns game.application
  (:require [clojure.core-ext :refer [edn-resource
                                      safe-merge
                                      define-order
                                      sort-by-order
                                      actions!
                                      reduce-actions!]] ; good library ✅, externalised
            [clojure.edn :as edn] ; good library ✅, externalised
            [clojure.gdx.fit-viewport :as fit-viewport] ; good library ✅, externalised
            [clojure.gdx.graphics.color :as color] ; good library ✅, externalised
            [clojure.gdx.graphics.colors :as colors] ; good library ✅, externalised
            [clojure.gdx.tiled-map-renderer :as tiled-map-renderer] ; good library ✅, externalised
            [clojure.gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager] ; good library ✅, externalised
            [clojure.gdx.scenes.scene2d.ui.text-button :as text-button]
            [clojure.gdx.orthographic-camera :as camera]
            [space.earlygrey.shape-drawer :as shape-drawer]
            [clojure.java.io :as io]
            [clojure.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.scenes.scene2d.event :as event]
            [clojure.gdx.scenes.scene2d.group :as group]
            [clojure.gdx.scenes.scene2d.ui.label :as label]
            [clojure.gdx.scenes.scene2d.ui.image :as image]
            [clojure.gdx.scenes.scene2d.ui.stack :as stack]
            [clojure.gdx.scenes.scene2d.ui.widget :as widget]
            [clojure.gdx.scenes.scene2d.ui.window :as window]
            [clojure.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [clojure.gdx.scenes.scene2d.stage :as stage]
            [clojure.scene2d.ui :as ui]

            [clojure.input.buttons :as input.buttons]
            [clojure.input.keys :as input.keys]

            [clojure.math.vector2 :as v]
            [clojure.string :as str]
            [clojure.gdx.viewport :as viewport]

            [game.impl.explored-tile-corners]
            [game.impl.grid]
            [game.impl.raycaster]
            [game.impl.textures]
            [game.impl.db]

            game.ui.data-viewer-window
            game.ui.error-window

            ; separate application ? better ! ?
            game.ui.property-editor-window
            game.ui.property-overview-window

            game.ui.dev-menu
            game.ui.action-bar
            game.ui.info-window

            [malli.core :as m]
            [malli.utils :as mu]

            [moon.ctx :as ctx]
            [moon.body :as body]
            [moon.content-grid :as content-grid]
            [moon.db :as db]
            [moon.creature-tiles]
            [moon.draws :as draws]
            [moon.effect :as effect]
            [moon.entity :as entity]
            [moon.grid :as grid]
            [moon.info :as info]
            [moon.inventory :as inventory]
            [moon.number :as number]
            [moon.raycaster :as raycaster]
            [moon.throwable :as throwable]
            [moon.state :as state]
            [moon.stats :as stats]
            [moon.skill :as skill]
            [moon.tiled-map :as tiled-map]
            [moon.timer :as timer]
            [moon.textures :as textures]
            [moon.txs :as txs]
            [moon.ui.action-bar :as action-bar]
            [moon.ui.inventory-window :as inventory-window]
            [moon.val-max :as val-max]
            [qrecord.core :as q]
            [reduce-fsm :as fsm])
  (:import (com.badlogic.gdx Application
                             ApplicationListener
                             Gdx)
           (com.badlogic.gdx.audio Sound)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.graphics GL20
                                      Pixmap
                                      Pixmap$Format
                                      Texture
                                      Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d BitmapFont
                                          SpriteBatch
                                          TextureRegion)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)
           (com.badlogic.gdx.utils Align
                                   Disposable))
  (:gen-class))

; Try only to be used here
; then make ctx protocols
; then see what's used together
; and data abstraction
(def schema
  (m/schema
   [:map {:closed true}
    ; Input, Audio, Files, Graphics
    [:ctx/app :some] ; ~~ only used in this ns ~~ ✅

    ; Audio, Files
    [:ctx/audio :some] ; ~~ only used in this ns ~~ ✅

    ; Graphics
    [:ctx/batch :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/cursors :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/default-font :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/unit-scale :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/world-unit-scale :some] ; ~~ only used in this ns ~~ ✅ -- accessed through ctx/world-unit-scale protocol
    [:ctx/world-viewport :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/shape-drawer :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/shape-drawer-texture :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/textures :some] ; 💥⚠️

    ; UI
    [:ctx/skin :some] ; 💥⚠️
    [:ctx/stage :some] ; 💥⚠️

    ; Frame
    [:ctx/active-entities :any] ; 💥⚠️
    [:ctx/delta-time :any] ; 💥⚠️
    [:ctx/mouseover-eid :any]
    [:ctx/ui-mouse-position :any]
    [:ctx/world-mouse-position :any]

    ; Constants
    [:ctx/colors :some]
    [:ctx/controls :some]
    [:ctx/controls-info :some]
    [:ctx/max-delta :some]
    [:ctx/max-speed :some]
    [:ctx/minimum-size :some]
    [:ctx/render-z-order :some]
    [:ctx/z-orders :some]

    ; Game
    ; The 'game' could be a separate library with no libgdx dependencies?
    [:ctx/content-grid :some]
    [:ctx/entity-ids :some]
    [:ctx/explored-tile-corners :some]
    [:ctx/factions-iterations :some]
    [:ctx/grid :some]
    [:ctx/id-counter :some]
    [:ctx/potential-field-cache :some]
    [:ctx/raycaster :some]
    [:ctx/start-position :some]
    [:ctx/tiled-map :some]
    [:ctx/db :some]
    [:ctx/elapsed-time :some]
    [:ctx/paused? :some]
    [:ctx/player-eid :some]
    ]))

(def world-fn-file
   "world_fns/modules.edn"
  ; "world_fns/vampire.edn"
  ; "world_fns/uf_caves.edn"
  )

(def draw-fns
  {
   :draw/circle           (fn
                            [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/circle! shape-drawer x y radius))
   :draw/ellipse          (fn
                            [{:keys [ctx/shape-drawer]} [x y] radius-x radius-y color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/ellipse! shape-drawer x y radius-x radius-y))
   :draw/filled-circle    (fn
                            [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/filled-circle! shape-drawer x y radius))
   :draw/filled-rectangle (fn
                            [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/filled-rectangle! shape-drawer x y w h))
   :draw/grid             (fn
                            [ctx leftx bottomy gridw gridh cellw cellh color-float-bits]
                            (let [w (* (float gridw) (float cellw))
                                  h (* (float gridh) (float cellh))
                                  topy (+ (float bottomy) (float h))
                                  rightx (+ (float leftx) (float w))]
                              (doseq [idx (range (inc (float gridw)))
                                      :let [linex (+ (float leftx) (* (float idx) (float cellw)))]]
                                (draws/handle ctx
                                              [[:draw/line [linex topy] [linex bottomy] color-float-bits]]))
                              (doseq [idx (range (inc (float gridh)))
                                      :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
                                (draws/handle ctx
                                              [[:draw/line [leftx liney] [rightx liney] color-float-bits]]))))
   :draw/line             (fn
                            [{:keys [ctx/shape-drawer]} [sx sy] [ex ey] color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/line! shape-drawer sx sy ex ey))
   :draw/rectangle        (fn
                            [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/rectangle! shape-drawer x y w h))
   :draw/sector           (fn
                            [{:keys [ctx/shape-drawer]} [center-x center-y] radius start-radians radians color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/sector! shape-drawer center-x center-y radius start-radians radians))
   :draw/text             (fn
                            [{:keys [ctx/batch
                                     ctx/unit-scale
                                     ctx/default-font]}
                             {:keys [font scale x y text h-align up?]}]
                            (let [^BitmapFont font (or font default-font)
                                  old-scale (.scaleX (.getData font))
                                  target-width 0
                                  wrap? false
                                  scale (* (float @unit-scale)
                                           (float (or scale 1)))]
                              (.setScale (.getData font) (* old-scale scale))
                              (.draw font
                                     batch
                                     text
                                     (float x)
                                     (float (+ y (if up?
                                                   (-> text
                                                       (str/split #"\n")
                                                       count
                                                       (* (.getLineHeight font)))
                                                   0)))
                                     (float target-width)
                                     Align/center
                                     wrap?)
                              (.setScale (.getData font) old-scale)))
   :draw/texture-region   (fn
                            [{:keys [^SpriteBatch ctx/batch
                                     ctx/unit-scale
                                     ctx/world-unit-scale]}
                             ^TextureRegion texture-region
                             [x y]
                             & {:keys [center? rotation]}]
                            (let [[w h] (let [dimensions [(.getRegionWidth  texture-region)
                                                          (.getRegionHeight texture-region)]]
                                          (if (= @unit-scale 1)
                                            dimensions
                                            (mapv (comp float (partial * world-unit-scale))
                                                  dimensions)))]
                              (if center?
                                (.draw batch
                                       texture-region
                                       (- (float x) (/ (float w) 2))
                                       (- (float y) (/ (float h) 2))
                                       (/ (float w) 2)
                                       (/ (float h) 2)
                                       w
                                       h
                                       1
                                       1
                                       (or rotation 0))
                                (.draw batch texture-region (float x) (float y) (float w) (float h)))))
   :draw/with-line-width  (fn
                            [{:keys [ctx/shape-drawer]
                              :as ctx}
                             width
                             draws]
                            (let [old-line-width (shape-drawer/default-line-width shape-drawer)]
                              (shape-drawer/set-default-line-width! shape-drawer (* width old-line-width))
                              (draws/handle ctx draws)
                              (shape-drawer/set-default-line-width! shape-drawer old-line-width)))
   })

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
                                    (Sound/.play (get sounds sound-name)))
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
  (world-unit-scale [ctx]
    (:ctx/world-unit-scale ctx))

  (mouse-position [{:keys [ctx/app]}]
    [(.getX (Application/.getInput app))
     (.getY (Application/.getInput app))])

  (button-just-pressed? [{:keys [ctx/app]} input-button]
    (.isButtonJustPressed (Application/.getInput app) input-button))

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
    (.isKeyJustPressed (Application/.getInput app) input-key))

  txs/Txs
  (handle! [ctx txs]
    (let [handled-txs (try (actions! txs-fn-map ctx txs)
                           (catch Throwable t
                             (throw (ex-info "Error handling txs"
                                             {:txs txs} t))))]
      (reduce-actions! reaction-txs-fn-map
                       ctx
                       handled-txs)))

  draws/Draws
  (handle [ctx draws]
    (doseq [{k 0 :as component} draws
            :when component]
      (apply (get draw-fns k) ctx (rest component))))
  )

(defn create-record [ctx]
  (merge (map->Context {}) ctx))

(defn validate [ctx]
  (mu/validate-humanize schema ctx)
  ctx)

(defn delta-time
  [{:keys [ctx/app]}]
  (.getDeltaTime (Application/.getGraphics app)))

(defn frames-per-second
  [{:keys [ctx/app]}]
  (.getFramesPerSecond (Application/.getGraphics app)))

(defn clear-screen!
  [{:keys [ctx/app] :as ctx}]
  (let [gl (.getGL20 (Application/.getGraphics app))]
    (.glClearColor gl 0 0 0 0)
    (.glClear gl GL20/GL_COLOR_BUFFER_BIT))
  ctx)

(defmethod state/cursor :player-dead
  [_ _eid _ctx]
  :cursors/black-x)

(defmethod state/cursor :active-skill
  [_ _eid _ctx]
  :cursors/sandclock)

(defmethod state/cursor :stunned
  [_ _eid _ctx]
  :cursors/denied)

(defmethod state/cursor :player-moving
  [_ _eid _ctx]
  :cursors/walking)

(defmethod state/cursor :player-idle
  [_ eid {:keys [ctx/interaction-state]}]
  (let [[k params] interaction-state]
    (case k
      :interaction-state/mouseover-actor
      (let [[actor-type params] params
            inventory-cell-with-item? (and (= actor-type :mouseover-actor/inventory-cell)
                                           (let [inventory-slot params]
                                             (get-in (:entity/inventory @eid) inventory-slot)))]
        (cond
         inventory-cell-with-item?
         :cursors/hand-before-grab

         (= actor-type :mouseover-actor/window-title-bar)
         :cursors/move-window

         (= actor-type :mouseover-actor/button)
         :cursors/over-button

         (= actor-type :mouseover-actor/unspecified)
         :cursors/default

         :else
         :cursors/default))

      :interaction-state/clickable-mouseover-eid
      (let [{:keys [clicked-eid
                    in-click-range?]} params]
        (case (:type (:entity/clickable @clicked-eid))
          :clickable/item (if in-click-range?
                            :cursors/hand-before-grab
                            :cursors/hand-before-grab-gray)
          :clickable/player :cursors/bag))

      :interaction-state.skill/usable
      :cursors/use-skill

      :interaction-state.skill/not-usable
      :cursors/skill-not-usable

      :interaction-state/no-skill-selected
      :cursors/no-skill-selected)))

(defn set-cursor!
  [{:keys [ctx/app
           ctx/cursors
           ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-key (state/cursor [state-k (state-k entity)] eid ctx)]
    (assert (contains? cursors cursor-key))
    (.setCursor (Application/.getGraphics app) (get cursors cursor-key)))
  ctx)

(defn key-pressed?
  [{:keys [ctx/app]} input-key]
  (.isKeyPressed (Application/.getInput app) input-key))

(defn dispose!
  [{:keys [ctx/audio
           ctx/batch
           ctx/cursors
           ctx/default-font
           ctx/shape-drawer-texture
           ctx/skin
           ctx/textures
           ctx/tiled-map]}]
  (run! Disposable/.dispose (vals audio))
  (Disposable/.dispose batch)
  (run! Disposable/.dispose (vals cursors))
  (Disposable/.dispose default-font)
  (Disposable/.dispose shape-drawer-texture)
  (Disposable/.dispose skin)
  (run! Disposable/.dispose (vals textures))
  (Disposable/.dispose tiled-map))

(defn- tile-color-setter*
  [{:keys [ray-blocked?
           explored-tile-corners
           light-position
           see-all-tiles?
           explored-tile-color
           visible-tile-color
           invisible-tile-color]}]
  #_(reset! do-once false)
  (let [light-cache (atom {})]
    (fn tile-color-setter [_color x y]
      (let [position [(int x) (int y)]
            explored? (get @explored-tile-corners position) ; TODO needs int call ?
            base-color (if explored?
                         explored-tile-color
                         invisible-tile-color)
            cache-entry (get @light-cache position :not-found)
            blocked? (if (= cache-entry :not-found)
                       (let [blocked? (ray-blocked? light-position position)]
                         (swap! light-cache assoc position blocked?)
                         blocked?)
                       cache-entry)]
        #_(when @do-once
            (swap! ray-positions conj position))
        (if blocked?
          (if see-all-tiles?
            visible-tile-color
            base-color)
          (do (when-not explored?
                (swap! explored-tile-corners assoc (mapv int position) true))
             visible-tile-color))))))

(comment
 (def ^:private count-rays? false)

 (def ray-positions (atom []))
 (def do-once (atom true))

 (count @ray-positions)
 2256
 (count (distinct @ray-positions))
 608
 (* 608 4)
 2432
 )

(defn- tile-color-setter
  [{:keys [ctx/colors
           ctx/explored-tile-corners
           ctx/raycaster
           ctx/world-viewport]}]
  (tile-color-setter*
   {:ray-blocked? (partial raycaster/blocked? raycaster)
    :explored-tile-corners explored-tile-corners
    :light-position (camera/position (:viewport/camera world-viewport))
    :see-all-tiles? false
    :explored-tile-color  (:colors/explored-tile colors)
    :visible-tile-color   (:colors/visible-tile colors)
    :invisible-tile-color (:colors/invisible-tile colors)}))

(defn draw-tiled-map!
  [{:keys [ctx/batch
           ctx/tiled-map
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}]
  (tiled-map-renderer/draw! batch
                            world-unit-scale
                            (:viewport/camera world-viewport)
                            tiled-map
                            (tile-color-setter ctx))
  ctx)

(defn camera-zoom [{:keys [ctx/world-viewport]}]
  (camera/zoom (:viewport/camera world-viewport)))

(defn visible-tiles [{:keys [ctx/world-viewport]}]
  (camera/visible-tiles (:viewport/camera world-viewport)))

(defn camera-frustum [{:keys [ctx/world-viewport]}]
  (camera/frustum (:viewport/camera world-viewport)))

(defn set-camera-position!
  [{:keys [ctx/player-eid
           ctx/world-viewport]
    :as ctx}]
  (camera/set-position! (:viewport/camera world-viewport)
                        (:body/position (:entity/body @player-eid)))
  ctx)

(defn update-mouse-positions
  [{:keys [ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (let [mp (ctx/mouse-position ctx)]
    (-> ctx
        (assoc :ctx/world-mouse-position (viewport/unproject world-viewport mp))
        (assoc :ctx/ui-mouse-position (-> stage :stage/viewport (viewport/unproject mp))))))

(def zoom-speed 0.025)

(defn window-camera-controls
  [{:keys [ctx/controls
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (when (key-pressed? ctx (:zoom-in controls))
    (camera/inc-zoom! (:viewport/camera world-viewport) zoom-speed))

  (when (key-pressed? ctx (:zoom-out controls))
    (camera/inc-zoom! (:viewport/camera world-viewport) (- zoom-speed)))

  (when (ctx/key-just-pressed? ctx (:close-windows-key controls))
    (->> (stage/find-actor stage "moon.ui.windows")
         group/children
         (run! #(actor/set-visible! % false))))

  (when (ctx/key-just-pressed? ctx (:toggle-inventory controls))
    (-> stage
        (stage/find-actor "moon.ui.windows.inventory")
        actor/toggle-visible!))

  (when (ctx/key-just-pressed? ctx (:toggle-entity-info controls))
    (-> stage
        (stage/find-actor "moon.ui.windows.entity-info")
        actor/toggle-visible!))
  ctx)

(defn resize!
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (viewport/update! (:stage/viewport stage) width height true)
  (viewport/update! world-viewport width height false))

(defn world-viewport-width
  [{:keys [ctx/world-viewport]}]
  (:viewport/world-width world-viewport))

(defn world-viewport-height
  [{:keys [ctx/world-viewport]}]
  (:viewport/world-height world-viewport))

(defn create-app [app]
  (tooltip-manager/set-initial-time! 0)
  (colors/put! {"PRETTY_NAME" [0.84 0.8 0.52 1]})
  (let [batch (SpriteBatch.)
        white-pixel-texture (let [pixmap (doto (Pixmap. 1 1 Pixmap$Format/RGBA8888)
                                           (.setColor 1 1 1 1)
                                           (.drawPixel 0 0))
                                  texture (Texture. pixmap)]
                              (.dispose pixmap)
                              texture)
        world-unit-scale (float (/ 48))]
    {:ctx/app app
     :ctx/audio (into {}
                      (for [sound-name (-> "sounds.edn" io/resource slurp edn/read-string)]
                        [sound-name
                         (.newSound (Application/.getAudio app)
                                    (.internal (Application/.getFiles app) (format "sounds/%s.wav" sound-name)))]))
     :ctx/batch batch
     :ctx/shape-drawer-texture white-pixel-texture
     :ctx/shape-drawer (shape-drawer/create batch (TextureRegion. white-pixel-texture 1 0 1 1))
     :ctx/default-font (let [path "exocet/films.EXL_____.ttf"
                             size 16
                             quality-scaling 2
                             generator (FreeTypeFontGenerator. (.internal (Application/.getFiles app) path))
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
                                   (let [pixmap (Pixmap. (.internal (Application/.getFiles app) (format path-format path)))
                                         cursor (.newCursor (Application/.getGraphics app) pixmap hotspot-x hotspot-y)]
                                     (.dispose pixmap)
                                     cursor))))
     :ctx/stage (let [stage (stage/create (fit-viewport/create 1440 900) batch)]
                  (.setInputProcessor (Application/.getInput app) stage)
                  stage)
     :ctx/skin (let [skin (Skin. (.internal (Application/.getFiles app) "uiskin.json"))]
                 (set! (.markupEnabled (-> skin
                                           (.getFont "default-font")
                                           .getData))
                       true)
                 skin)
     :ctx/unit-scale (atom 1)}))

(defn create-textures
  [ctx]
  (assoc ctx :ctx/textures (game.impl.textures/create (Application/.getFiles (:ctx/app ctx)))))

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

(defn player-movement-vector [ctx]
  (let [r (when (key-pressed? ctx input.keys/d) [1  0])
        l (when (key-pressed? ctx input.keys/a) [-1 0])
        u (when (key-pressed? ctx input.keys/w) [0  1])
        d (when (key-pressed? ctx input.keys/s) [0 -1])]
    (when (or r l u d)
      (let [v (v/normalise (reduce v/add [0 0] (remove nil? [r l u d])))]
        (when (pos? (v/length v))
          v)))))

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
  (assoc ctx :ctx/db (game.impl.db/create)))

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
  {:type :ui/menu
   :menus [
           {:label "Ctx Data"
            :items [{:label "Show data"
                     :on-click (fn [_actor {:keys [ctx/skin
                                                   ctx/stage] :as ctx}]
                                 (stage/add-actor! stage
                                                   (actor/create
                                                    {:type :ui/data-viewer-window
                                                     :title "Data View"
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
                                                    (actor/create
                                                     {:type :ui/property-overview-window
                                                      :db db
                                                      :textures textures
                                                      :skin skin
                                                      :property-type property-type
                                                      :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                                                       (stage/add-actor! stage
                                                                                         (actor/create
                                                                                          {:type :ui/property-editor-window
                                                                                           :ctx ctx
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
                                      #_(Disposable/.dispose (:ctx/tiled-map ctx))
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
   :skin skin})

(defn create-action-bar [_ctx]
  {:type :ui/action-bar})

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
    {:type :ui/actor
     :draw! (fn [this _batch _parent-alpha]
              (when-let [stage (actor/stage this)]
                (draws/handle (:stage/ctx stage)
                              (create-draws (:stage/ctx stage)))))}))

(defn create-windows [ctx actor-fns]
  {:type :ui/group
   :group/actors (for [f actor-fns]
                   (f ctx))
   :actor/name "moon.ui.windows"})

(defn create-player-state-draw [_ctx]
  {:type :ui/actor
   :draw! (fn [this _batch _parent-alpha]
            (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (actor/stage this))
                  entity @player-eid
                  state-k (:state (:entity/fsm entity))]
              (draws/handle ctx (state/draw-ui-view [state-k (state-k entity)] player-eid ctx))))})

(defn create-player-message-actor [_ctx]
  (let [message-duration-seconds 0.5]
    {:type :ui/actor
     :actor/name "player-message"
     :actor/user-object (atom nil)
     :draw! (fn [this _batch _parent-alpha]
              (when-let [stage (actor/stage this)]
                (draws/handle (:stage/ctx stage)
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
                   (reset! state nil)))))}))

(defn create-info-window
  [{:keys [ctx/skin
           ctx/stage]}]
  (actor/create
   {:type :ui/info-window
    :title "Entity Info"
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

(defmethod state/clicked-inventory-cell :player-idle
  [_ eid cell]
  (when-let [item (get-in (:entity/inventory @eid) cell)]
    [[:tx/sound "bfxr_takeit"]
     [:tx/event eid :pickup-item item]
     [:tx/remove-item eid cell]]))

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
                                                            (txs/handle! ctx
                                                                         (state/clicked-inventory-cell [state-k (state-k entity)]
                                                                                                       player-eid
                                                                                                       cell))))}
                      :group/actors [(widget/create
                                      {:draw! (fn [this _batch _parent-alpha]
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
                                                                                  (actor/user-object (actor/parent this)))))))})
                                     (image/create
                                      {:content background-drawable
                                       :actor/name "image-widget"
                                       :actor/user-object {:background-drawable background-drawable
                                                           :cell-size cell-size}})]})}))]
    (window/create
     {:title "Inventory"
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
                       (:viewport/world-height (:stage/viewport stage))]})))

(defn spawn-player
  [{:keys [ctx/db
           ctx/entity-ids
           ctx/start-position]
    :as ctx}]
  (txs/handle! ctx
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
  (txs/handle!
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
  (doseq [[actor-fn & params] [[create-dev-menu]
                               [create-action-bar]
                               [create-hp-mana-bar]
                               [create-windows [create-info-window
                                                create-inventory-window]]
                               [create-player-state-draw]
                               [create-player-message-actor]]]
    (stage/add-actor! stage (actor/create (apply actor-fn ctx params))))
  ctx)

(defn create-grid
  [{:keys [ctx/tiled-map]
    :as ctx}]
  (assoc ctx :ctx/grid (game.impl.grid/create tiled-map)))

(defn create-content-grid
  [{:keys [ctx/tiled-map]
    :as ctx}]
  (assoc ctx :ctx/content-grid (content-grid/create (.get (.getProperties tiled-map) "width")
                                                    (.get (.getProperties tiled-map) "height")
                                                    16)))

(defn create-explored-tile-corners
  [{:keys [ctx/tiled-map]
    :as ctx}]
  (assoc ctx :ctx/explored-tile-corners (game.impl.explored-tile-corners/create tiled-map)))

(defn create-raycaster
  [{:keys [ctx/grid] :as ctx}]
  (assoc ctx :ctx/raycaster (game.impl.raycaster/create grid)))

(defn create! [app]
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

(defn get-stage-ctx
  [{:keys [ctx/stage]
    :as ctx}]
  (or (:stage/ctx stage)
      ctx)) ; first render stage does not have ctx set.

(defn update-mouseover-eid
  [{:keys [ctx/mouseover-eid
           ctx/stage
           ctx/player-eid
           ctx/grid
           ctx/raycaster
           ctx/render-z-order
           ctx/world-mouse-position]
    :as ctx}]
  (let [mouseover-actor (stage/mouseover-actor stage (ctx/mouse-position ctx))
        position world-mouse-position
        new-eid (if mouseover-actor
                  nil
                  (let [player @player-eid
                        hits (remove #(= (:body/z-order (:entity/body @%)) :z-order/effect)
                                     (grid/point->entities grid position))]
                    (->> render-z-order
                         (sort-by-order hits #(:body/z-order (:entity/body @%)))
                         reverse
                         (filter #(raycaster/line-of-sight? raycaster player @%))
                         first)))]
    (when mouseover-eid
      (swap! mouseover-eid dissoc :entity/mouseover?))
    (when new-eid
      (swap! new-eid assoc :entity/mouseover? true))
    (assoc ctx :ctx/mouseover-eid new-eid)))

(defn check-debug-viewer
  [{:keys [ctx/controls
           ctx/mouseover-eid
           ctx/skin
           ctx/stage
           ctx/grid
           ctx/world-mouse-position]
    :as ctx}]
  (when (ctx/button-just-pressed? ctx (:open-debug-button controls))
    (let [data (or (and mouseover-eid @mouseover-eid)
                   @(grid (mapv int world-mouse-position)))]
      (stage/add-actor! stage
                        (actor/create
                         {:type :ui/data-viewer-window
                          :title "Data View"
                          :data data
                          :width 500
                          :height 500
                          :skin skin}))))
  ctx)

(defn set-active-entities
  [{:keys [ctx/player-eid
           ctx/content-grid]
    :as ctx}]
  (assoc ctx :ctx/active-entities
         (content-grid/active-entities content-grid @player-eid)))

(defn- mouseover-actor-info [actor]
  (let [inventory-slot (and (actor/parent actor)
                            (= "inventory-cell" (actor/name (actor/parent actor)))
                            (actor/user-object (actor/parent actor)))]
    (cond
     inventory-slot            [:mouseover-actor/inventory-cell inventory-slot]
     (ui/window-title-bar? actor) [:mouseover-actor/window-title-bar]
     (ui/button? actor)           [:mouseover-actor/button]
     :else                     [:mouseover-actor/unspecified])))

(defn- player-effect-ctx [mouseover-eid world-mouse-position player-eid]
  (let [target-position (or (and mouseover-eid
                                 (:body/position (:entity/body @mouseover-eid)))
                            world-mouse-position)]
    {:effect/source player-eid
     :effect/target mouseover-eid
     :effect/target-position target-position
     :effect/target-direction (v/direction (:body/position (:entity/body @player-eid))
                                           target-position)}))


(defn- interaction-state
  [stage
   world-mouse-position
   mouseover-eid
   player-eid
   mouseover-actor]
  (cond
   mouseover-actor
   [:interaction-state/mouseover-actor (mouseover-actor-info mouseover-actor)]

   (and mouseover-eid
        (:entity/clickable @mouseover-eid))
   [:interaction-state/clickable-mouseover-eid
    {:clicked-eid mouseover-eid
     :in-click-range? (< (body/distance (:entity/body @player-eid)
                                        (:entity/body @mouseover-eid))
                         (:entity/click-distance-tiles @player-eid))}]

   :else
   (if-let [skill-id (-> stage
                         (stage/find-actor "moon.ui.action-bar")
                         action-bar/selected-skill)]
     (let [entity @player-eid
           skill (skill-id (:entity/skills entity))
           effect-ctx (player-effect-ctx mouseover-eid world-mouse-position player-eid)
           state (skill/usable-state skill entity effect-ctx)]
       (if (= state :usable)
         [:interaction-state.skill/usable [skill effect-ctx]]
         [:interaction-state.skill/not-usable state]))
     [:interaction-state/no-skill-selected])))

(defn assoc-interaction-state
  [{:keys [ctx/mouseover-eid
           ctx/stage
           ctx/player-eid
           ctx/world-mouse-position]
    :as ctx}]
  (assoc ctx :ctx/interaction-state (interaction-state stage
                                                       world-mouse-position
                                                       mouseover-eid
                                                       player-eid
                                                       (stage/mouseover-actor stage (ctx/mouse-position ctx)))))

(defn- creature-speed [{:keys [entity/stats]}]
  (or (stats/get-stat-value stats :stats/movement-speed)
      0))

(defmethod state/handle-input :player-moving
  [_ eid ctx]
  (if-let [movement-vector (player-movement-vector ctx)]
    [[:tx/assoc eid :entity/movement {:direction movement-vector
                                      :speed (creature-speed @eid)}]]
    [[:tx/event eid :no-movement-input]]))

(defn- interaction-state->txs [[k params] stage player-eid]
  (case k
    :interaction-state/mouseover-actor nil

    :interaction-state/clickable-mouseover-eid
    (let [{:keys [clicked-eid
                  in-click-range?]} params]
      (if in-click-range?
        (case (:type (:entity/clickable @clicked-eid))
          :clickable/player
          [[:tx/toggle-inventory-visible]]

          :clickable/item
          (let [item (:entity/item @clicked-eid)]
            (cond
             (-> stage
                 (stage/find-actor "moon.ui.windows.inventory")
                 actor/visible?)
             [[:tx/sound "bfxr_takeit"]
              [:tx/mark-destroyed clicked-eid]
              [:tx/event player-eid :pickup-item item]]

             (inventory/can-pickup-item? (:entity/inventory @player-eid) item)
             [[:tx/sound "bfxr_pickup"]
              [:tx/mark-destroyed clicked-eid]
              [:tx/pickup-item player-eid item]]

             :else
             [[:tx/sound "bfxr_denied"]
              [:tx/show-message "Your Inventory is full"]])))
        [[:tx/sound "bfxr_denied"]
         [:tx/show-message "Too far away"]]))

    :interaction-state.skill/usable
    (let [[skill effect-ctx] params]
      [[:tx/event player-eid :start-action [skill effect-ctx]]])

    :interaction-state.skill/not-usable
    (let [state params]
      [[:tx/sound "bfxr_denied"]
       [:tx/show-message (case state
                           :cooldown "Skill is still on cooldown"
                           :not-enough-mana "Not enough mana"
                           :invalid-params "Cannot use this here")]])

    :interaction-state/no-skill-selected
    [[:tx/sound "bfxr_denied"]
     [:tx/show-message "No selected skill"]]))

(defmethod state/handle-input :player-idle
  [_ player-eid {:keys [ctx/interaction-state
                        ctx/stage] :as ctx}]
  (if-let [movement-vector (player-movement-vector ctx)]
    [[:tx/event player-eid :movement-input movement-vector]]
    (when (ctx/button-just-pressed? ctx input.buttons/left)
      (interaction-state->txs interaction-state
                              stage
                              player-eid))))


(defn handle-player-state-input!
  [{:keys [ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (state/handle-input [state-k (state-k entity)] eid ctx)]
    (txs/handle! ctx txs))
  ctx)

(defn dissoc-interaction-state [ctx]
  (dissoc ctx :ctx/interaction-state))

(defmethod state/pause-game? :active-skill
  [_]
  false)

(defmethod state/pause-game? :stunned
  [_]
  false)

(defmethod state/pause-game? :player-moving
  [_]
  false)

(defmethod state/pause-game? :player-idle
  [_]
  true)

(defmethod state/pause-game? :player-dead
  [_]
  true)

(def pausing? true)

(defn assoc-paused
  [{:keys [ctx/controls
           ctx/player-eid]
    :as ctx}]
  (assoc ctx :ctx/paused?
         (or #_error
             (and pausing?
                  (state/pause-game? (:state (:entity/fsm @player-eid)))
                  (not (or (ctx/key-just-pressed? ctx (:unpause-once controls))
                           (key-pressed? ctx (:unpause-continously controls))))))))

(defn update-time
  [{:keys [ctx/max-delta]
    :as ctx}]
  (let [delta-ms (min (delta-time ctx) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))

(defn update-potential-fields!
  [{:keys [ctx/active-entities
           ctx/factions-iterations
           ctx/grid
           ctx/potential-field-cache]
    :as ctx}]
  (doseq [[faction max-iterations] factions-iterations]
    (grid/tick! grid
                potential-field-cache
                faction
                active-entities
                max-iterations))
  ctx)

(defn tick-entities!
  [{:keys [ctx/active-entities
           ctx/skin
           ctx/stage]
    :as ctx}]
  (try
   ; TODO should not have effects which interfere in eid order (remove?)
   ; convert attacks self!?
   (txs/handle! ctx (mapcat (fn [eid]
                              (mapcat (fn [[k v]]
                                        (comment
                                         :ctx/delta-time
                                         :ctx/grid
                                         :ctx/raycaster
                                         :ctx/max-speed
                                         :ctx/elapsed-time
                                         :ctx/raycaster
                                         ; effect/useful? what is used
                                         )
                                        (try (entity/tick [k v] eid ctx)
                                             (catch Throwable t
                                               (throw (ex-info "Error at `entity/tick`:" {:eid eid} t)))))
                                      @eid))
                            active-entities))
   (catch Throwable t
     (throwable/pretty-pst t)
     (stage/add-actor! stage
                       (actor/create
                        {:type :ui/error-window
                         :skin skin
                         :throwable t}))))
  ctx)

(defn if-not-paused-steps
  [{:keys [ctx/paused?]
    :as ctx}]
  (if paused?
    ctx
    (-> ctx
        update-time
        update-potential-fields!
        tick-entities!)))

(defmethod entity/destroy :entity/destroy-audiovisual
  [[_ audiovisuals-id] eid]
  [[:tx/audiovisual
    (:body/position (:entity/body @eid))
    audiovisuals-id]])

(defn remove-destroyed-entities!
  [ctx]
  (txs/handle! ctx (mapcat
                    (fn [eid]
                      (cons
                       [:tx/unregister-eid eid]
                       (mapcat (fn [[k v]]
                                 (entity/destroy [k v] eid))
                               @eid)))
                    (filter (comp :entity/destroyed? deref)
                            (vals @(:ctx/entity-ids ctx)))))
  ctx)

(defn update-draw-stage
  [{:keys [ctx/stage] :as ctx}]
  (stage/set-ctx! stage ctx)
  (stage/act!  stage)
  (stage/draw! stage)
  (:stage/ctx stage))

(defn draw-tile-grid
  [ctx]
  (let [[left-x _right-x bottom-y _top-y] (camera-frustum ctx)]
    [[:draw/grid
      (int left-x)
      (int bottom-y)
      (inc (int (world-viewport-width ctx)))
      (+ 2 (int (world-viewport-height ctx)))
      1
      1
      (color/float-bits [1 1 1 0.8])]]))

(def ^:dbg-flag show-potential-field-colors? false) ; :good, :evil
(def ^:dbg-flag show-cell-entities? false)
(def ^:dbg-flag show-cell-occupied? false)

(defn draw-cell-debug
  [{:keys [ctx/colors
           ctx/grid
           ctx/factions-iterations]
    :as ctx}]
  (apply concat
         (for [[x y] (visible-tiles ctx)
               :let [cell (grid [x y])]
               :when cell
               :let [cell* @cell]]
           [(when (and show-cell-entities? (seq (:entities cell*)))
              [:draw/filled-rectangle x y 1 1 (:colors/debug-cell-entities colors)])
            (when (and show-cell-occupied? (seq (:occupied cell*)))
              [:draw/filled-rectangle x y 1 1 (:colors/debug-cell-occupied colors)])
            (when-let [faction show-potential-field-colors?]
              (let [{:keys [distance]} (faction cell*)]
                (when distance
                  (let [ratio (/ distance (factions-iterations faction))]
                    [:draw/filled-rectangle x y 1 1 ((:colors/debug-potential-field colors) ratio)]))))])))

(def ^:private render-layers
  [#{:entity/mouseover?
     :stunned
     :player-item-on-cursor}
   #{:entity/clickable
     :entity/animation
     :entity/image
     :entity/line-render}
   #{:npc-sleeping
     :entity/temp-modifier
     :entity/string-effect}
   #{:entity/stats
     :active-skill}])

(def ^:dbg-flag show-body-bounds? false)

(defn- draw-body-rect [{:keys [body/position body/width body/height]} color-float-bits]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    [[:draw/rectangle x y width height color-float-bits]]))

(defn- draw-entity
  [{:keys [ctx/colors] :as ctx} entity render-layer]
  (try (do
        (when show-body-bounds?
          (draws/handle ctx (draw-body-rect (:entity/body entity)
                                             (if (:body/collides? (:entity/body entity))
                                               (:colors/debug-body-outline-collides colors)
                                               (:colors/debug-body-outline colors)))))
        (doseq [[k v] entity
                :when (get render-layer k)]
          (draws/handle ctx (entity/render [k v] entity ctx))))
       (catch Throwable t
         (draws/handle ctx (draw-body-rect (:entity/body entity) (:colors/debug-body-outline-render-error colors)))
         (throwable/pretty-pst t))))

(defn draw-entities!
  [{:keys [ctx/active-entities
           ctx/player-eid
           ctx/raycaster
           ctx/render-z-order]
    :as ctx}]
  (let [entities (map deref active-entities)
        player @player-eid
        should-draw? (fn [entity z-order]
                       (or (= z-order :z-order/effect)
                           (raycaster/line-of-sight? raycaster player entity)))]
    (doseq [[z-order entities] (sort-by-order (group-by (comp :body/z-order :entity/body) entities)
                                              first
                                              render-z-order)
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (draw-entity ctx entity render-layer))))

(defn highlight-mouseover-tile
  [{:keys [ctx/colors
           ctx/grid
           ctx/world-mouse-position]}]
  (let [[x y] (mapv int world-mouse-position)
        cell (grid [x y])]
    (when (and cell (#{:air :none} (:movement @cell)))
      [[:draw/rectangle x y 1 1
        (case (:movement @cell)
          :air  (:colors/mouseover-tile-air colors)
          :none (:colors/mouseover-tile-none colors))]])))

(defn draw-on-world-viewport!
  [{:keys [^SpriteBatch ctx/batch
           ctx/shape-drawer
           ctx/unit-scale
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}]
  ; fix scene2d.ui.tooltip flickering
  ; _everything_ flickers with TextToolTip!
  ; it changes batch color somehow and does not change it back ! FIXME
  (.setColor batch 1 1 1 1)
  ;
  (.setProjectionMatrix batch (camera/combined (:viewport/camera world-viewport)))
  (.begin batch)
  (let [old-line-width (shape-drawer/default-line-width shape-drawer)]
    (shape-drawer/set-default-line-width! shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [f [
               #_draw-tile-grid
               draw-cell-debug
               draw-entities!
               #_moon.geom-test
               highlight-mouseover-tile
               ]]
      (draws/handle ctx (f ctx)))
    (reset! unit-scale 1)
    (shape-drawer/set-default-line-width! shape-drawer old-line-width))
  (.end batch)
  ctx)

(defn render! [ctx]
  (-> ctx
      get-stage-ctx
      validate
      update-mouse-positions
      update-mouseover-eid
      check-debug-viewer
      set-active-entities
      set-camera-position!
      clear-screen!
      draw-tiled-map!
      draw-on-world-viewport!
      assoc-interaction-state
      set-cursor!
      handle-player-state-input!
      dissoc-interaction-state
      assoc-paused
      if-not-paused-steps
      remove-destroyed-entities!
      window-camera-controls
      update-draw-stage
      validate))

(def state (atom nil))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (reify ApplicationListener
                        (create [_]
                          (reset! state (create! Gdx/app)))

                        (dispose [_]
                          (dispose! @state))

                        (render [_]
                          (swap! state render!))

                        (resize [_ width height]
                          (resize! @state width height))

                        (pause [_])

                        (resume [_]))
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "Moon")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))
