(ns moon.game
  (:require [clojure.audio :as audio]
            [clojure.audio.sound :as sound]
            [clojure.disposable :as disposable]
            [clojure.edn :as edn]
            [clojure.files :as files]
            [clojure.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [clojure.gdx.scene2d.stage]
            [clojure.gdx.scene2d.ui.skin :as skin]
            [clojure.gdx.scene2d.ui.tooltip-manager :as tooltip-manager]
            [clojure.gdx.shape-drawer]
            [clojure.gdx.maps.tiled.renderer :as tiled-map-renderer]
            [clojure.gdx.orthographic-camera :as orthographic-camera]
            [clojure.gdx.viewport]
            [clojure.graphics :as graphics]
            [clojure.graphics.batch :as batch]
            [clojure.graphics.bitmap-font :as bitmap-font]
            [clojure.graphics.color :as color]
            [clojure.graphics.freetype :as freetype]
            [clojure.graphics.texture :as texture]
            [clojure.graphics.texture-region :as texture-region]
            [clojure.graphics.orthographic-camera :as camera]
            [clojure.graphics.viewport :as viewport]
            [clojure.graphics.shape-drawer :as shape-drawer]
            [clojure.input :as input]
            [clojure.java.io :as io]
            [clojure.scene2d.actor :as actor]
            [clojure.scene2d.stage :as stage]
            [clojure.string :as str]
            [clojure.math.vector2 :as v]
            [moon.action-bar :as action-bar]
            [moon.body :as body]
            [moon.cell :as cell]
            [moon.creature-tiles]
            [moon.content-grid :as content-grid]
            [moon.db :as db]
            [moon.draws :as draws]
            [moon.effect :as effect]
            [moon.entity :as entity]
            [moon.grid :as grid]
            [moon.grid2d :as g2d]
            [moon.info :as info]
            [moon.inventory :as inventory]
            [moon.inventory-window :as inventory-window]
            [moon.skill :as skill]
            [moon.malli :as m]
            [moon.raycaster :as raycaster]
            [moon.start :refer [edn-resource]]
            [moon.state :as state]
            [moon.stats :as stats]
            [moon.throwable :as throwable]
            [moon.tiled-map :as tiled-map]
            [moon.timer :as timer]
            [moon.input]
            [moon.order :as order]
            moon.impl.textures
            [moon.textures :as textures]
            [moon.map :as map]
            [moon.txs :as txs]
            [qrecord.core :as q]
            [reduce-fsm :as fsm]
            moon.ui-actors.dev-menu
            moon.ui-actors.action-bar
            moon.ui-actors.player-message
            moon.ui-actors.player-state-draw
            moon.ui-actors.windows.inventory
            moon.ui-actors.windows.info
            moon.ui-actors.hp-mana-bar
            )
  (:import (com.badlogic.gdx Input)))

(defn- draw-text! [font batch {:keys [scale text x y up? h-align target-width wrap?]}]
  (let [old-scale (bitmap-font/scale-x font)]
    (bitmap-font/set-scale! font (* old-scale scale))
    (bitmap-font/draw! font
                       batch
                       text
                       x
                       (+ y (if up? (bitmap-font/text-height font text) 0))
                       target-width
                       (or h-align :align/center)
                       wrap?)
    (bitmap-font/set-scale! font old-scale)))

(q/defrecord Entity [entity/body])

(defn- send-event! [ctx eid event params]
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
         [:tx/state-enter eid new-state-obj]]))))

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
   :tx/event                    (fn
                                  ([ctx eid event]
                                   (send-event! ctx eid event nil))
                                  ([ctx eid event params]
                                   (send-event! ctx eid event params)))
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
   :tx/unregister-eid           (fn [{:keys [ctx/content-grid
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
   :tx/spawn-line               (fn [_ctx {:keys [start end duration color thick?]}]
                                  [[:tx/spawn-effect
                                    start
                                    {:entity/line-render {:thick? thick? :end end :color color}
                                     :entity/delete-after-duration duration}]])
   :tx/move-entity              (fn [{:keys [ctx/content-grid
                                             ctx/grid]}
                                     eid]
                                  (content-grid/position-changed! content-grid eid)
                                  (grid/remove-from-touched-cells! grid eid)
                                  (grid/set-touched-cells! grid eid)
                                  (when (:body/collides? (:entity/body @eid))
                                    (grid/remove-from-occupied-cells! grid eid)
                                    (grid/set-occupied-cells! grid eid))
                                  nil)
   :tx/spawn-projectile         (fn [_ctx
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
   :tx/spawn-creature           (fn [_ctx
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
                                        (map/safe-merge components))]])
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
   })

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
                                                    (actor/create
                                                     {:type :ui/window
                                                      :title title
                                                      :skin skin
                                                      :window/modal? true
                                                      :table/rows [[{:actor (actor/create
                                                                             {:type :ui/label
                                                                              :text text
                                                                              :skin skin})}]
                                                                   [{:actor (actor/create
                                                                             {:type :ui/text-button
                                                                              :text button-text
                                                                              :skin skin
                                                                              :actor/listeners {:listener/change (fn [_event _actor]
                                                                                                                   (actor/remove! (stage/find-actor stage "moon.ui.modal-window"))
                                                                                                                   (on-click))}})}]]
                                                      :actor/name "moon.ui.modal-window"
                                                      :actor/position [(/ (viewport/world-width  (stage/viewport stage)) 2)
                                                                       (* (viewport/world-height (stage/viewport stage)) (/ 3 4))
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

   #_(remove-skill! [stage skill-id]
                    (-> stage
                        (stage/find-actor "moon.ui.action-bar")
                        (action-bar/remove-skill! skill-id)))
   })

(defn- actions!
  [txs-fn-map ctx txs]
  (loop [ctx ctx
         txs txs
         handled-txs []]
    (if (empty? txs)
      handled-txs
      (let [[k & params :as tx] (first txs)]
        (if tx
          (let [_ (assert (vector? tx))
                f (get txs-fn-map k)
                _ (assert f (str "Cannot find function for tx: " k))
                new-txs (try
                         (apply f ctx params)
                         (catch Throwable t
                           (throw (ex-info "Error handling tx"
                                           {:tx tx}
                                           t))))]
            (recur ctx
                   (concat (or new-txs []) (rest txs))
                   (conj handled-txs tx)))
          (recur ctx
                 (rest txs)
                 handled-txs))))))

(defn- reduce-actions!
  [txs-fn-map ctx txs]
  (loop [ctx ctx
         txs txs]
    (if (empty? txs)
      ctx
      (let [[k & params :as tx] (first txs)]
        (if tx
          (let [_ (assert (vector? tx))
                f (get txs-fn-map k)
                new-ctx (try
                         (if (nil? f)
                           ctx
                           (apply f ctx params))
                         (catch Throwable t
                           (throw (ex-info "Error handling tx"
                                           {:tx tx}
                                           t))))]
            (recur new-ctx
                   (rest txs)))
          (recur ctx
                 (rest txs)))))))

(def draw-fns
  {
   :draw/circle           (fn
                            [{:keys [ctx/shape-drawer]} position radius color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/circle! shape-drawer position radius))
   :draw/ellipse          (fn
                            [{:keys [ctx/shape-drawer]} position radius-x radius-y color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/ellipse! shape-drawer position radius-x radius-y))
   :draw/filled-circle    (fn
                            [{:keys [ctx/shape-drawer]} position radius color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/filled-circle! shape-drawer position radius))
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
                            [{:keys [ctx/shape-drawer]} start end color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/line! shape-drawer start end))
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
                            (draw-text! (or font default-font)
                                        batch
                                        {:scale (* (float @unit-scale)
                                                   (float (or scale 1)))
                                         :text text
                                         :x x
                                         :y y
                                         :up? up?
                                         :h-align h-align
                                         :target-width 0
                                         :wrap? false}))
   :draw/texture-region   (fn
                            [{:keys [ctx/batch
                                     ctx/unit-scale
                                     ctx/world-unit-scale]}
                             texture-region
                             [x y]
                             & {:keys [center? rotation]}]
                            (let [[w h] (let [dimensions [(texture-region/width  texture-region)
                                                          (texture-region/height texture-region)]]
                                          (if (= @unit-scale 1)
                                            dimensions
                                            (mapv (comp float (partial * world-unit-scale))
                                                  dimensions)))]
                              (if center?
                                (batch/draw! batch
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
                                (batch/draw! batch texture-region x y w h))))
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

(q/defrecord Context []
  txs/TransactionHandler
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

(def black [0 0 0 1])
(def white [1 1 1 1])
(def gray  [0.5 0.5 0.5 1])
(def red   [1 0 0 1])

(def outline-alpha 0.4)

(defn- hpbar-color [ratio]
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

(defn- load-colors []
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
   :colors/hp-bar hpbar-color
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
   }
  )

(def schema
  (m/schema
   [:map {:closed true}
    [:ctx/app :some]
    [:ctx/active-entities :any]
    [:ctx/audio :some]
    [:ctx/batch :some]
    [:ctx/colors :some]
    [:ctx/content-grid :some]
    [:ctx/controls :some]
    [:ctx/controls-info :some]
    [:ctx/cursors :some]
    [:ctx/db :some]
    [:ctx/default-font :some]
    [:ctx/delta-time :any]
    [:ctx/elapsed-time :some]
    [:ctx/entity-ids :some]
    [:ctx/explored-tile-corners :some]
    [:ctx/factions-iterations :some]
    [:ctx/files :some]
    [:ctx/graphics :some]
    [:ctx/grid :some]
    [:ctx/id-counter :some]
    [:ctx/input :some]
    [:ctx/max-delta :some]
    [:ctx/max-speed :some]
    [:ctx/minimum-size :some]
    [:ctx/mouseover-eid :any]
    [:ctx/paused? :some]
    [:ctx/player-eid :some]
    [:ctx/potential-field-cache :some]
    [:ctx/raycaster :some]
    [:ctx/render-z-order :some]
    [:ctx/shape-drawer :some]
    [:ctx/shape-drawer-texture :some]
    [:ctx/skin :some]
    [:ctx/stage :some]
    [:ctx/start-position :some]
    [:ctx/textures :some]
    [:ctx/tiled-map :some]
    [:ctx/ui-mouse-position :any]
    [:ctx/ui-viewport :some]
    [:ctx/unit-scale :some]
    [:ctx/world-mouse-position :any]
    [:ctx/world-unit-scale :some]
    [:ctx/world-viewport :some]
    [:ctx/z-orders :some]
    ]))

(defn create!
  [ctx]
  (tooltip-manager/set-initial-time! 0)
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          ctx
          [
           [(fn [{:keys [ctx/audio ctx/files] :as ctx}]
              (assoc ctx :ctx/audio
                     (into {}
                           (for [sound-name (-> "sounds.edn" io/resource slurp edn/read-string)]
                             [sound-name
                              (audio/new-sound audio (files/internal files (format "sounds/%s.wav" sound-name)))]))))]
           [(fn [ctx]
              (assoc ctx :ctx/batch (sprite-batch/create)))]
           [(fn [{:keys [ctx/graphics] :as ctx}]
              (assoc ctx :ctx/shape-drawer-texture (graphics/white-pixel-texture graphics)))]
           [(fn [{:keys [ctx/batch
                         ctx/shape-drawer-texture]
                  :as ctx}]
              (assoc ctx :ctx/shape-drawer (clojure.gdx.shape-drawer/create batch (texture/region shape-drawer-texture 1 0 1 1))))]
           [(fn [ctx]
              (assoc ctx :ctx/ui-viewport (clojure.gdx.viewport/create 1440 900 (orthographic-camera/create))))]
           [(fn [{:keys [ctx/batch
                         ctx/ui-viewport]
                  :as ctx}]
              (assoc ctx :ctx/stage (clojure.gdx.scene2d.stage/create ui-viewport batch)))]
           [(fn [{:keys [ctx/input
                         ctx/stage]
                  :as ctx}]
              (input/set-processor! input stage)
              ctx)]
           [(fn [{:keys [ctx/files] :as ctx}]
              (assoc ctx :ctx/skin (let [skin (skin/create (files/internal files "uiskin.json"))]
                                     (bitmap-font/enable-markup! (skin/font skin "default-font") true)
                                     skin)))]
           [(fn [{:keys [ctx/graphics
                         ctx/files]
                  :as ctx}]
              (assoc ctx :ctx/cursors (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
                                        (update-vals data
                                                     (fn [[path [hotspot-x hotspot-y]]]
                                                       (graphics/new-cursor graphics
                                                                            (files/internal files (format path-format path))
                                                                            hotspot-x
                                                                            hotspot-y))))))]
           [(fn [ctx]
              (assoc ctx :ctx/textures (moon.impl.textures/create ctx {:folder "resources/"
                                                                       :extensions #{"png" "bmp"}})))]
           [(fn [ctx]
              (assoc ctx :ctx/world-unit-scale (float (/ 48))))]
           [(fn [{:keys [ctx/world-unit-scale] :as ctx}]
              (assoc ctx :ctx/world-viewport
                     (let [world-width  (* 1440  world-unit-scale)
                           world-height (* 900 world-unit-scale)]
                       (clojure.gdx.viewport/create world-width
                                                    world-height
                                                    (doto (orthographic-camera/create)
                                                      (orthographic-camera/set-to-ortho! false world-width world-height))))))]
           [(fn [{:keys [ctx/app
                         ctx/files]
                  :as ctx}]
              (assoc ctx :ctx/default-font (let [{:keys [path
                                                         size
                                                         quality-scaling
                                                         enable-markup?
                                                         use-integer-positions?]} {
                                                                                   :path "exocet/films.EXL_____.ttf"
                                                                                   :size 16
                                                                                   :quality-scaling 2
                                                                                   :enable-markup? true
                                                                                   :use-integer-positions? false
                                                                                   ; :texture-filter/linear because scaling to world-units
                                                                                   :min-filter :linear
                                                                                   :mag-filter :linear
                                                                                   }]
                                             (doto (freetype/generate-font app
                                                                           (files/internal files path)
                                                                           {:size (* size quality-scaling)})
                                               (bitmap-font/set-scale! (/ quality-scaling))
                                               (bitmap-font/enable-markup! enable-markup?)
                                               (bitmap-font/use-integer-positions! use-integer-positions?))))
              )]
           [(fn [ctx]
              (assoc ctx
                     :ctx/controls {
                                    :zoom-in :input.keys/minus
                                    :zoom-out :input.keys/equals
                                    :unpause-once :input.keys/p
                                    :unpause-continously :input.keys/space
                                    :close-windows-key :input.keys/escape
                                    :toggle-inventory  :input.keys/i
                                    :toggle-entity-info :input.keys/e
                                    :open-debug-button :input.buttons/right
                                    }
                     :ctx/controls-info (str/join "\n"
                                                  ["[W][A][S][D] - Move"
                                                   "[ESCAPE] - Close windows"
                                                   "[I] - Inventory window"
                                                   "[E] - Entity Info window"
                                                   "[-]/[=] - Zoom"
                                                   "[P]/[SPACE] - Unpause"
                                                   "rightclick on tile or entity - open debug data window"
                                                   "Leftmouse click - use skill/drop item on cursor"])
                     )
              )]

           [(fn [ctx]
              (assoc ctx :ctx/colors (load-colors)))]

           [(fn [ctx]
              (assoc ctx
                     :ctx/active-entities nil
                     :ctx/delta-time nil
                     :ctx/mouseover-eid nil
                     :ctx/ui-mouse-position nil
                     :ctx/world-mouse-position nil
                     :ctx/elapsed-time 0
                     :ctx/paused? false
                     :ctx/unit-scale (atom 1)
                     :ctx/factions-iterations {:good 15 :evil 5}
                     :ctx/max-delta 0.04
                     :ctx/minimum-size 0.39
                     :ctx/z-orders [:z-order/on-ground
                                    :z-order/ground
                                    :z-order/flying
                                    :z-order/effect]
                     ))]

           [(fn [{:keys [ctx/z-orders]
                  :as ctx}]
              (assoc ctx :ctx/render-z-order (order/define-order z-orders)))]

           [(fn
              [{:keys [ctx/minimum-size
                       ctx/max-delta]
                :as ctx}]
              (assoc ctx :ctx/max-speed (/ minimum-size max-delta)))]

           [(fn [ctx]
              (assoc ctx :ctx/db (db/create {:schemas "schema.edn"
                                             :properties "properties.edn"})))]

           [(fn [ctx]
              (merge (map->Context {}) ctx))]

           [(fn [ctx]
              (doseq [actor [(moon.ui-actors.dev-menu/create ctx)
                             (moon.ui-actors.action-bar/create)
                             (moon.ui-actors.hp-mana-bar/create ctx)
                             (actor/create
                              {:type :ui/group
                               :group/actors [(moon.ui-actors.windows.info/create ctx)
                                              (moon.ui-actors.windows.inventory/create ctx)]
                               :actor/name "moon.ui.windows"})
                             (moon.ui-actors.player-state-draw/create)
                             (moon.ui-actors.player-message/create)]]
                (stage/add-actor! (:ctx/stage ctx) actor))
              ctx)]

           [(fn
              [{:keys [ctx/db
                       ctx/textures]
                :as ctx}]
              (let [[f params] (edn-resource "world_fns/modules.edn"
                                             ; "world_fns/vampire.edn"
                                             ; "world_fns/uf_caves.edn"
                                             )
                    {:keys [tiled-map
                            start-position]} (f
                                              (assoc params
                                                     :level/creature-properties (moon.creature-tiles/prepare
                                                                                 (db/all-raw db :properties/creatures)
                                                                                 #(textures/texture-region textures %))
                                                     :textures textures))]
                (assoc ctx
                       :ctx/tiled-map tiled-map
                       :ctx/start-position start-position)))]

           [(fn [{:keys [ctx/tiled-map] :as ctx}]
              (assoc ctx :ctx/grid (g2d/create-grid (tiled-map/width tiled-map)
                                                    (tiled-map/height tiled-map)
                                                    (fn [position]
                                                      (atom (cell/create position
                                                                         (case (tiled-map/movement-property tiled-map position)
                                                                           "none" :none
                                                                           "air"  :air
                                                                           "all"  :all)))))))]

           [(fn
              [{:keys [ctx/tiled-map]
                :as ctx}]
              (assoc ctx :ctx/content-grid (content-grid/create (tiled-map/width tiled-map)
                                                                (tiled-map/height tiled-map)
                                                                16)))]

           [(fn [{:keys [ctx/tiled-map] :as ctx}]
              (assoc ctx :ctx/explored-tile-corners (atom (g2d/create-grid (tiled-map/width tiled-map)
                                                                           (tiled-map/height tiled-map)
                                                                           (constantly false)))))]

           [(fn [{:keys [ctx/grid] :as ctx}]
              (assoc ctx :ctx/raycaster (let [width  (g2d/width  grid)
                                              height (g2d/height grid)
                                              cells  (for [cell (map deref (g2d/cells grid))]
                                                       [(:position cell)
                                                        (boolean (cell/blocks-vision? cell))])]
                                          (let [arr (make-array Boolean/TYPE width height)]
                                            (doseq [[[x y] blocked?] cells]
                                              (aset arr x y (boolean blocked?)))
                                            [arr width height]))))]

           [(fn [ctx]
              (assoc ctx :ctx/potential-field-cache (atom nil)))]

           [(fn [ctx]
              (assoc ctx :ctx/id-counter (atom 0)))]

           [(fn [ctx]
              (assoc ctx :ctx/entity-ids (atom {})))]

           [(fn
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
                (assoc ctx :ctx/player-eid eid)))]

           [(fn
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
              ctx)]
           ]
          ))

(defn dispose!
  [{:keys [ctx/audio
           ctx/batch
           ctx/cursors
           ctx/default-font
           ctx/shape-drawer-texture
           ctx/skin
           ctx/textures
           ctx/tiled-map]}]
  (run! disposable/dispose! (vals audio))
  (disposable/dispose! batch)
  (run! disposable/dispose! (vals cursors))
  (disposable/dispose! default-font)
  (disposable/dispose! shape-drawer-texture)
  (disposable/dispose! skin)
  (run! disposable/dispose! (vals textures))
  (disposable/dispose! tiled-map)
  nil)

(defn render! [ctx render-fns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          ctx
          (concat
           [
            [(fn
               [{:keys [ctx/stage]
                 :as ctx}]
               (or (stage/ctx stage)
                   ctx))] ; first render stage does not have ctx set.

            [(fn [ctx]
               (m/validate-humanize schema ctx)
               ctx)]

            [(fn
               [{:keys [ctx/input
                        ctx/ui-viewport
                        ctx/world-viewport]
                 :as ctx}]
               (let [mp (input/mouse-position input)]
                 (-> ctx
                     (assoc :ctx/world-mouse-position (viewport/unproject world-viewport mp))
                     (assoc :ctx/ui-mouse-position (viewport/unproject ui-viewport mp)))))]

            [(fn
               [{:keys [ctx/input
                        ctx/mouseover-eid
                        ctx/stage
                        ctx/player-eid
                        ctx/grid
                        ctx/raycaster
                        ctx/render-z-order
                        ctx/world-mouse-position]
                 :as ctx}]
               (let [mouseover-actor (stage/mouseover-actor stage (input/mouse-position input))
                     position world-mouse-position
                     new-eid (if mouseover-actor
                               nil
                               (let [player @player-eid
                                     hits (remove #(= (:body/z-order (:entity/body @%)) :z-order/effect)
                                                  (grid/point->entities grid position))]
                                 (->> render-z-order
                                      (order/sort-by-order hits #(:body/z-order (:entity/body @%)))
                                      reverse
                                      (filter #(raycaster/line-of-sight? raycaster player @%))
                                      first)))]
                 (when mouseover-eid
                   (swap! mouseover-eid dissoc :entity/mouseover?))
                 (when new-eid
                   (swap! new-eid assoc :entity/mouseover? true))
                 (assoc ctx :ctx/mouseover-eid new-eid)))]

            [(fn
               [{:keys [ctx/controls
                        ctx/input
                        ctx/mouseover-eid
                        ctx/skin
                        ctx/stage
                        ctx/grid
                        ctx/world-mouse-position]
                 :as ctx}]
               (when (input/button-just-pressed? input (:open-debug-button controls))
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
               ctx)]

            [(fn
               [{:keys [ctx/player-eid
                        ctx/content-grid]
                 :as ctx}]
               (assoc ctx :ctx/active-entities
                      (content-grid/active-entities content-grid @player-eid)))]

            [(fn
               [{:keys [ctx/player-eid
                        ctx/world-viewport]
                 :as ctx}]
               (camera/set-position! (viewport/camera world-viewport)
                                     (:body/position (:entity/body @player-eid)))
               ctx)]

            [(fn [ctx]
               (graphics/clear! (:ctx/graphics ctx) 0 0 0 0)
               ctx)]

            ]
           render-fns)))

(defn resize!
  [{:keys [ctx/ui-viewport
           ctx/world-viewport]}
   width height]
  (viewport/update! ui-viewport width height true)
  (viewport/update! world-viewport width height false)
  nil)

(extend-type Input
  moon.input/Input
  (key-pressed? [input key]
    (input/key-pressed? input key))

  (key-just-pressed? [input key]
    (input/key-just-pressed? input key))

  (button-just-pressed? [input button]
    (input/button-just-pressed? input button))

  (mouse-position [input]
    (input/mouse-position input))

  (player-movement-vector [input]
    (let [r (when (input/key-pressed? input :input.keys/d) [1  0])
          l (when (input/key-pressed? input :input.keys/a) [-1 0])
          u (when (input/key-pressed? input :input.keys/w) [0  1])
          d (when (input/key-pressed? input :input.keys/s) [0 -1])]
      (when (or r l u d)
        (let [v (v/add-vs (remove nil? [r l u d]))]
          (when (pos? (v/length v))
            v))))))


; no window movable type cursor appears here like in player idle
; inventory still working, other stuff not, because custom listener to keypresses ? use actor listeners?
; => input events handling
; hmmm interesting ... can disable @ item in cursor  / moving / etc.

(comment
 (.postRunnable com.badlogic.gdx.Gdx/app
                (fn []
                  (:tx/show-modal @moon.application/state
                       {:title "TestTitle"
                        :text "TextTEXT"
                        :button-text "testbuttonTEXT"
                        :on-click (fn [])})))

 )


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
    :light-position (camera/position (viewport/camera world-viewport))
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
                            (viewport/camera world-viewport)
                            tiled-map
                            (tile-color-setter ctx))
  ctx)

(defn draw-on-world-viewport!
  [{:keys [ctx/batch
           ctx/shape-drawer
           ctx/unit-scale
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}
   draw-fns]
  ; fix scene2d.ui.tooltip flickering
  ; _everything_ flickers with TextToolTip!
  ; it changes batch color somehow and does not change it back ! FIXME
  (batch/set-color! batch 1 1 1 1)
  ;
  (batch/set-projection-matrix! batch (camera/combined (viewport/camera world-viewport)))
  (batch/begin! batch)
  (let [old-line-width (shape-drawer/default-line-width shape-drawer)]
    (shape-drawer/set-default-line-width! shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [f draw-fns]
      (draws/handle ctx (f ctx)))
    (reset! unit-scale 1)
    (shape-drawer/set-default-line-width! shape-drawer old-line-width))
  (batch/end! batch)
  ctx)

(def ^:dbg-flag show-potential-field-colors? false) ; :good, :evil
(def ^:dbg-flag show-cell-entities? false)
(def ^:dbg-flag show-cell-occupied? false)

(defn draw-cell-debug
  [{:keys [ctx/colors
           ctx/grid
           ctx/factions-iterations
           ctx/world-viewport]}]
  (apply concat
         (for [[x y] (camera/visible-tiles (viewport/camera world-viewport))
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


(def ^:private render-layers ; TODO move external - simple TODO/checklist / state pass
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

(def ^:dbg-flag show-body-bounds? false) ; TODO same here?

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

(defn draw-entities
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
    (doseq [[z-order entities] (order/sort-by-order (group-by (comp :body/z-order :entity/body) entities)
                                                    first
                                                    render-z-order)
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (draw-entity ctx entity render-layer))))

(defn draw-tile-grid
  [{:keys [ctx/world-viewport]}]
  (let [[left-x _right-x bottom-y _top-y] (camera/frustum (viewport/camera world-viewport))]
    [[:draw/grid
      (int left-x)
      (int bottom-y)
      (inc (int (viewport/world-width  world-viewport)))
      (+ 2 (int (viewport/world-height world-viewport)))
      1
      1
      [1 1 1 0.8]]]))

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

(defn- mouseover-actor-info [actor]
  (let [inventory-slot (and (actor/parent actor)
                            (= "inventory-cell" (actor/name (actor/parent actor)))
                            (actor/user-object (actor/parent actor)))]
    (cond
     inventory-slot            [:mouseover-actor/inventory-cell inventory-slot]
     (actor/window-title-bar? actor) [:mouseover-actor/window-title-bar]
     (actor/button?           actor) [:mouseover-actor/button]
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

(defn- assoc-interaction-state
  [{:keys [ctx/input
           ctx/mouseover-eid
           ctx/stage
           ctx/player-eid
           ctx/world-mouse-position]
    :as ctx}]
  (assoc ctx :ctx/interaction-state (interaction-state stage
                                                       world-mouse-position
                                                       mouseover-eid
                                                       player-eid
                                                       (stage/mouseover-actor stage (input/mouse-position input)))))

(defn- set-cursor
  [{:keys [ctx/cursors
           ctx/graphics
           ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-key (state/cursor [state-k (state-k entity)] eid ctx)]
    (assert (contains? cursors cursor-key))
    (graphics/set-cursor! graphics (get cursors cursor-key)))
  ctx)

(defn- player-state-handle-input
  [{:keys [ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (state/handle-input [state-k (state-k entity)] eid ctx)]
    (txs/handle! ctx txs))
  ctx)

(defn update-player-state [ctx]
  (-> ctx
      assoc-interaction-state
      set-cursor
      player-state-handle-input
      (dissoc :ctx/interaction-state)))

(def pausing? true) ; TODO FIXME

(defn assoc-paused
  [{:keys [ctx/controls
           ctx/input
           ctx/player-eid]
    :as ctx}]
  (assoc ctx :ctx/paused?
         (or #_error
             (and pausing?
                  (state/pause-game? (:state (:entity/fsm @player-eid)))
                  (not (or (input/key-just-pressed? input (:unpause-once controls))
                           (input/key-pressed? input (:unpause-continously controls))))))))

(defn if-not-paused
  [{:keys [ctx/paused?]
    :as ctx}
   fns]
  (if paused?
    ctx
    (reduce (fn [ctx [f & params]]
              (apply f ctx params))
            ctx
            fns)))

(defn remove-destroyed-entities
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
