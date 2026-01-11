; TODO
; * editor in 1 namespace
(ns moon.application
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.math.vector2 :as v]
            [clojure.string :as str]
            [moon.ui.actor :as actor]
            [moon.ui.group :as group]
            [moon.ui.stage :as stage]
            [malli.core :as m]
            [malli.utils :as mu]
            [moon.audio :as audio]
            [moon.color :as color]
            [moon.db :as db]
            [moon.effect :as effect]
            [moon.effects.target-all :as target-all]
            [moon.effects.target-entity :as target-entity]
            [moon.animation :as animation] ; just animation
            [moon.body :as body]
            [moon.faction :as faction]
            [moon.entity.inventory :as inventory]
            [moon.entity.skills :as skills]
            [moon.skill :as skill]
            [moon.entity.state :as state] ; 'creature' ?
            [moon.entity.state.player-item-on-cursor :as player-item-on-cursor]
            [moon.entity.stats :as stats] ; moon.stats ?
            [moon.graphics :as graphics]
            [moon.input :as input]
            [moon.throwable :as throwable]
            [moon.ui :as ui]
            [moon.ui.action-bar :as action-bar]
            [moon.ui.build.editor-window :as editor-window]
            [moon.ui.editor.overview-window :as overview-window]
            [moon.ui.info-window :as info-window]
            [moon.ui.message :as message]
            [moon.ui.widget :as widget]
            [moon.ui.texture-region-drawable :as texture-region-drawable]
            [moon.utils :as utils]
            [moon.timer :as timer]
            [moon.val-max :as val-max]
            [moon.world :as world]
            [moon.world.content-grid :as content-grid]
            [moon.world.grid :as grid]
            [moon.world.info :as info]
            [moon.world-fns.creature-tiles]
            [moon.world.tiled-map :as tiled-map]
            [moon.world.raycaster :as raycaster]
            [moon.ui.dev-menu :as dev-menu]
            moon.ui.editor.window
            moon.ui.editor.widgets-impl
            [moon.ui.image :as image]
            [moon.ui.stack :as stack]
            [moon.ui.table :as table]
            [moon.ui.window :as window]
            moon.entity.state-impl
            [moon.tx-handler :as tx-handler]
            [qrecord.core :as q]
            [reduce-fsm :as fsm])
  (:import (com.badlogic.gdx ApplicationListener
                             Audio
                             Files
                             Gdx
                             Input
                             Input$Buttons)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Color)
           (com.badlogic.gdx.scenes.scene2d Event)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)
           (com.badlogic.gdx.scenes.scene2d.utils ClickListener)
           (com.badlogic.gdx.utils Disposable)
           (moon Stage))
  (:gen-class))

(defn- edn-resource [path]
  (->> path
       io/resource
       slurp
       edn/read-string))

(def ^:private schema
  (m/schema
   [:map {:closed true}
    [:ctx/audio :some]
    [:ctx/graphics :some]
    [:ctx/input :some]
    [:ctx/stage :some]
    [:ctx/skin :some]
    [:ctx/db :some]
    [:ctx/world :some]]))

(defn- validate [ctx]
  (mu/validate-humanize schema ctx)
  ctx)

(defn- call-world-fn
  [world-fn creature-properties graphics]
  (let [[f params] (edn-resource world-fn)]
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
                            :entity/inventory                       moon.entity.inventory/create!
                            :entity/skills                          moon.entity.skills/create!}

   })



(def reaction-txs-fn-map
  {
   :tx/sound                    (fn
                                  [{:keys [ctx/audio] :as ctx} sound-name]
                                  (audio/play! audio sound-name)
                                  ctx)
   :tx/toggle-inventory-visible (fn
                                  [{:keys [ctx/stage] :as ctx}]
                                  (ui/toggle-inventory-visible! stage)
                                  ctx)
   :tx/show-message             (fn
                                  [{:keys [ctx/stage] :as ctx} message]
                                  (ui/show-text-message! stage message)
                                  ctx)
   :tx/show-modal               (fn
                                  [{:keys [ctx/skin
                                           ctx/stage]
                                    :as ctx}
                                   opts]
                                  (ui/show-modal-window! stage skin (stage/viewport stage) opts)
                                  ctx)
   :tx/set-item                 (fn
                                  [{:keys [ctx/graphics
                                           ctx/skin
                                           ctx/stage]
                                    :as ctx}
                                   eid cell item]
                                  (when (:entity/player? @eid)
                                    (ui/set-item! stage cell
                                                  {:texture-region (graphics/texture-region graphics (:entity/image item))
                                                   :tooltip-text (info/text item nil)}
                                                  skin))
                                  ctx)
   :tx/remove-item              (fn
                                  [{:keys [ctx/stage] :as ctx} eid cell]
                                  (when (:entity/player? @eid)
                                    (ui/remove-item! stage cell))
                                  ctx)
   :tx/add-skill                (fn
                                  [{:keys [ctx/graphics
                                           ctx/skin
                                           ctx/stage]
                                    :as ctx}
                                   eid skill]
                                  (when (:entity/player? @eid)
                                    (ui/add-skill! stage
                                                   {:skill-id (:property/id skill)
                                                    :texture-region (graphics/texture-region graphics (:entity/image skill))
                                                    :tooltip-text (fn [{:keys [ctx/world]}]
                                                                    (info/text skill world))}
                                                   skin))
                                  ctx)
   })

(defn- world-move-entity
  [{:keys [world/content-grid
           world/grid]}
   eid body direction rotate-in-movement-direction?]
  (content-grid/position-changed! content-grid eid)
  (grid/remove-from-touched-cells! grid eid)
  (grid/set-touched-cells! grid eid)
  (when (:body/collides? (:entity/body @eid))
    (grid/remove-from-occupied-cells! grid eid)
    (grid/set-occupied-cells! grid eid))
  (swap! eid assoc-in [:entity/body :body/position] (:body/position body))
  (when rotate-in-movement-direction?
    (swap! eid assoc-in [:entity/body :body/rotation-angle] (v/angle-from-vector direction)))
  nil)

(defn- world-handle-event
  ([world eid event]
   (world-handle-event world eid event nil))
  ([world eid event params]
   (let [fsm (:entity/fsm @eid)
         _ (assert fsm)
         old-state-k (:state fsm)
         new-fsm (fsm/fsm-event fsm event)
         new-state-k (:state new-fsm)]
     (when-not (= old-state-k new-state-k)
       (let [old-state-obj (let [k (:state (:entity/fsm @eid))]
                             [k (k @eid)])
             new-state-obj [new-state-k (state/create [new-state-k params] eid world)]]
         [[:tx/assoc       eid :entity/fsm new-fsm]
          [:tx/assoc       eid new-state-k (new-state-obj 1)]
          [:tx/dissoc      eid old-state-k]
          [:tx/state-exit  eid old-state-obj]
          [:tx/state-enter eid new-state-obj]])))))

(defn- create-component [[k v] {:keys [world/create-fns] :as world}]
  (if-let [f (create-fns k)]
    (f v world)
    v))

(defn- after-create-component [[k v] eid {:keys [world/after-create-fns] :as world}]
  (when-let [f (after-create-fns k)]
    (f v eid world)))

(q/defrecord Entity [entity/body])

(defn- world-spawn-entity
 [{:keys [world/content-grid
          world/entity-ids
          world/grid
          world/id-counter
          world/spawn-entity-schema]
   :as world}
  entity]
 (mu/validate-humanize spawn-entity-schema entity)
 (let [entity (reduce (fn [m [k v]]
                        (assoc m k (create-component [k v] world)))
                      {}
                      entity)
       _ (assert (and (not (contains? entity :entity/id))))
       entity (assoc entity :entity/id (swap! id-counter inc))
       entity (merge (map->Entity {}) entity)
       eid (atom entity)]
   (let [id (:entity/id @eid)]
     (assert (number? id))
     (swap! entity-ids assoc id eid))
   (content-grid/add-entity! content-grid eid)
   ; https://github.com/damn/core/issues/58
   ;(assert (valid-position? grid @eid))
   (grid/set-touched-cells! grid eid)
   (when (:body/collides? (:entity/body @eid))
     (grid/set-occupied-cells! grid eid))
   (mapcat #(after-create-component % eid world) @eid)))

; TODO just call fns? no need 'transactions'??
; or only 1 'game/sprite-batch' ? moon.sprite-batch ?

(def txs-fn-map
  {
   ;; FIXME only this passes ctx, otherwise 'world' only
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
   ;;

   :tx/assoc                    (fn
                                  [_ctx eid k value]
                                  (swap! eid assoc k value)
                                  nil)

   :tx/assoc-in                 (fn
                                  [_ctx eid ks value]
                                  (swap! eid assoc-in ks value)
                                  nil)

   :tx/dissoc                   (fn
                                  [_ctx eid k]
                                  (swap! eid dissoc k)
                                  nil)

   :tx/update                   (fn
                                  [_ctx eid & params]
                                  (apply swap! eid update params)
                                  nil)

   :tx/mark-destroyed           (fn
                                  [_ctx eid]
                                  (swap! eid assoc :entity/destroyed? true)
                                  nil)

   :tx/set-cooldown             (fn
                                  [{:keys [ctx/world]} eid skill]
                                  (swap! eid update :entity/skills skills/set-cooldown skill (:world/elapsed-time world))
                                  nil)

   :tx/add-text-effect          (fn
                                  [{:keys [ctx/world]} eid text duration]
                                  [[:tx/assoc
                                    eid
                                    :entity/string-effect
                                    (if-let [existing (:entity/string-effect @eid)]
                                      (-> existing
                                          (update :text str "\n" text)
                                          (update :counter timer/increment duration))
                                      {:text text
                                       :counter (timer/create (:world/elapsed-time world) duration)})]])

   :tx/add-skill                (fn
                                  [_ctx eid {:keys [property/id] :as skill}]
                                  {:pre [(not (contains? (:entity/skills @eid) id))]}
                                  (swap! eid update :entity/skills assoc id skill)
                                  nil)

   :tx/set-item                 (fn
                                  [_ctx eid cell item]
                                  (let [entity @eid
                                        inventory (:entity/inventory entity)]
                                    (assert (and (nil? (get-in inventory cell))
                                                 (inventory/valid-slot? cell item)))
                                    (swap! eid assoc-in (cons :entity/inventory cell) item)
                                    (when (inventory/applies-modifiers? cell)
                                      (swap! eid update :entity/stats stats/add (:stats/modifiers item)))
                                    nil))

   :tx/remove-item              (fn
                                  [_ctx eid cell]
                                  (let [entity @eid
                                        item (get-in (:entity/inventory entity) cell)]
                                    (assert item)
                                    (swap! eid assoc-in (cons :entity/inventory cell) nil)
                                    (when (inventory/applies-modifiers? cell)
                                      (swap! eid update :entity/stats stats/remove-mods (:stats/modifiers item)))
                                    nil))
   :tx/pickup-item              (fn
                                  [_ctx eid item]
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
                                  [{:keys [ctx/world]} & params]
                                  (apply world-handle-event world params))
   :tx/state-enter              (fn [_ctx eid [state-k state-v]]
                                  (state/enter [state-k state-v] eid))
   :tx/effect                   (fn [{:keys [ctx/world]} effect-ctx effects]
                                  (mapcat #(effect/handle % effect-ctx world)
                                          (filter #(effect/applicable? % effect-ctx) effects)))
   :tx/spawn-alert              (fn
                                  [{:keys [ctx/world]} position faction duration]
                                  [[:tx/spawn-effect
                                    position
                                    {:entity/alert-friendlies-after-duration
                                     {:counter (timer/create (:world/elapsed-time world) duration)
                                      :faction faction}}]])
   :tx/spawn-line               (fn
                                  [_ctx {:keys [start end duration color thick?]}]
                                  [[:tx/spawn-effect
                                    start
                                    {:entity/line-render {:thick? thick? :end end :color color}
                                     :entity/delete-after-duration duration}]])
   :tx/move-entity              (fn
                                  [{:keys [ctx/world]} & params]
                                  (apply world-move-entity world params))
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
   :tx/spawn-effect             (fn
                                  [{:keys [ctx/world]}
                                   position
                                   components]
                                  [[:tx/spawn-entity
                                    (assoc components
                                           :entity/body (assoc (:world/effect-body-props world) :position position))]])

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
                                        (utils/safe-merge components))]])
   :tx/spawn-entity             (fn [{:keys [ctx/world]} entity]
                                  (world-spawn-entity world entity))
   :tx/sound                    (fn [& params] nil)
   :tx/toggle-inventory-visible (fn [& params] nil)
   :tx/show-message             (fn [& params] nil)
   :tx/show-modal               (fn [& params] nil)
   })

(defn reduce-actions!
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

(defn handle! [ctx txs]
  (let [handled-txs (try (tx-handler/actions! txs-fn-map ctx txs)
                         (catch Throwable t
                           (throw (ex-info "Error handling txs"
                                           {:txs txs} t))))]
    (reduce-actions! reaction-txs-fn-map
                     ctx
                     handled-txs)))

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
                         (doto (texture-region-drawable/create (slot->texture-region slot))
                           (.setMinSize cell-size cell-size)
                           (texture-region-drawable/tint (Color. 1 1 1 0.4))))
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
                                     (handle! ctx txs)))))
      :slot->texture-region slot->texture-region})))

(defn- create-ui-windows
  [graphics skin stage]
  (group/create
   {:actor/name "moon.ui.windows"
    :group/actors [(create-info-window skin stage)
                   (create-inventory-window graphics skin stage)]}))

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

(def message-duration-seconds 0.5)

(defn- create-player-message-actor []
  (message/create message-duration-seconds))

(defn- load-sounds
  [audio files {:keys [sound-names path-format]}]
  (let [sound-name->file-handle (into {}
                                      (for [sound-name (edn-resource sound-names)
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

(defn- create!
  [{:keys [gdx/audio
           gdx/files
           gdx/graphics
           gdx/input]}
   config]
  (let [db (db/create)
        graphics (graphics/create! graphics files (:graphics config))
        stage (ui/create! graphics)
        skin (create-skin (Files/.internal files "uiskin.json"))
        ctx (merge (map->Context {})
                   {:ctx/audio (load-sounds audio files (:audio config))
                    :ctx/db db
                    :ctx/graphics graphics
                    :ctx/input input
                    :ctx/stage stage
                    :ctx/skin skin})]
    (Input/.setInputProcessor input stage)
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
          _ (handle! ctx
                     [[:tx/spawn-creature (let [{:keys [creature-id
                                                        components]} (:world/player-components world)]
                                            {:position (mapv (partial + 0.5) (:world/start-position world))
                                             :creature-property (db/build db creature-id)
                                             :components components})]])
          ctx (let [eid (get @(:world/entity-ids world) 1)]
                (assert (:entity/player? @eid))
                (assoc-in ctx [:ctx/world :world/player-eid] eid))]
      (handle!
       ctx
       (for [[position creature-id] (tiled-map/spawn-positions (:world/tiled-map world))]
         [:tx/spawn-creature {:position (mapv (partial + 0.5) position)
                              :creature-property (db/build db (keyword creature-id))
                              :components (:world/enemy-components world)}]))

      ctx)))

(defn- dispose!
  [{:keys [ctx/audio
           ctx/graphics
           ctx/skin
           ctx/stage
           ctx/world]
    :as ctx}]
  (audio/dispose! audio)
  (graphics/dispose! graphics)
  (ui/dispose! stage)
  (Disposable/.dispose skin)
  (world/dispose! world)
  ctx)

(defn- resize!
  [{:keys [ctx/graphics] :as ctx} width height]
  (graphics/update-ui-viewport! graphics width height)
  (graphics/update-world-vp! graphics width height)
  ctx)

(defn- get-stage-ctx
  [{:keys [ctx/stage]
    :as ctx}]
  (or (ui/get-ctx stage)
      ctx)) ; first render stage does not have ctx set.

(defn- update-mouse
  [{:keys [ctx/graphics
           ctx/input]
    :as ctx}]
  (let [mp (input/mouse-position input)]
    (-> ctx
        (assoc-in [:ctx/graphics :graphics/world-mouse-position] (graphics/unproject-world graphics mp))
        (assoc-in [:ctx/graphics :graphics/ui-mouse-position] (graphics/unproject-ui graphics mp)))))

(defn- update-mouseover-eid
  [{:keys [ctx/graphics
           ctx/input
           ctx/stage
           ctx/world]
    :as ctx}]
  (let [mouseover-actor (ui/mouseover-actor stage (input/mouse-position input))
        new-eid (if mouseover-actor
                  nil
                  (world/mouseover-entity world (:graphics/world-mouse-position graphics)))]
    (when-let [mouseover-eid (:world/mouseover-eid world)]
      (swap! mouseover-eid dissoc :entity/mouseover?))
    (when new-eid
      (swap! new-eid assoc :entity/mouseover? true))
    (assoc-in ctx [:ctx/world :world/mouseover-eid] new-eid)))

(defn- check-open-debug
  [{:keys [ctx/graphics
           ctx/input
           ctx/skin
           ctx/stage
           ctx/world]
    :as ctx}]
  (when (input/button-just-pressed? input (:open-debug-button input/controls))
    (let [data (or (and (:world/mouseover-eid world) @(:world/mouseover-eid world))
                   @((:world/grid world) (mapv int (:graphics/world-mouse-position graphics))))]
      (ui/show-data-viewer! stage data skin)))
  ctx)

(defn- assoc-active-entities
  [{:keys [ctx/world]
    :as ctx}]
  (update ctx :ctx/world world/cache-active-entities))

(defn- set-camera-on-player
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (graphics/set-position! graphics (world/player-position world))
  ctx)

(defn- clear-screen!
  [{:keys [ctx/graphics] :as ctx}]
  (graphics/clear-screen! graphics color/black)
  ctx)

(defn- tile-color-setter
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

(defn- draw-world-map!
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (graphics/draw-tiled-map! graphics
                            (:world/tiled-map world)
                            (tile-color-setter
                             {:ray-blocked? (partial raycaster/blocked? world)
                              :explored-tile-corners (:world/explored-tile-corners world)
                              :light-position (graphics/position graphics)
                              :see-all-tiles? false
                              :explored-tile-color  [0.5 0.5 0.5 1]
                              :visible-tile-color   [1 1 1 1]
                              :invisible-tile-color [0 0 0 1]}))
  ctx)

(def ^:dbg-flag show-tile-grid? false)

(defn draw-tile-grid
  [{:keys [ctx/graphics]}]
  (when show-tile-grid?
    (let [[left-x _right-x bottom-y _top-y] (graphics/frustum graphics)]
      [[:draw/grid
        (int left-x)
        (int bottom-y)
        (inc (int (graphics/world-vp-width  graphics)))
        (+ 2 (int (graphics/world-vp-height graphics)))
        1
        1
        [1 1 1 0.8]]])))

(defn- highlight-mouseover-tile
  [{:keys [ctx/graphics
           ctx/world]}]
  (let [[x y] (mapv int (:graphics/world-mouse-position graphics))
        cell ((:world/grid world) [x y])]
    (when (and cell (#{:air :none} (:movement @cell)))
      [[:draw/rectangle x y 1 1
        (case (:movement @cell)
          :air  [1 1 0 0.5]
          :none [1 0 0 0.5])]])))

(def ^:dbg-flag show-potential-field-colors? false) ; :good, :evil
(def ^:dbg-flag show-cell-entities? false)
(def ^:dbg-flag show-cell-occupied? false)

(defn- draw-cell-debug
  [{:keys [ctx/graphics
           ctx/world]}]
  (apply concat
         (for [[x y] (graphics/visible-tiles graphics)
               :let [cell ((:world/grid world) [x y])]
               :when cell
               :let [cell* @cell]]
           [(when (and show-cell-entities? (seq (:entities cell*)))
              [:draw/filled-rectangle x y 1 1 [1 0 0 0.6]])
            (when (and show-cell-occupied? (seq (:occupied cell*)))
              [:draw/filled-rectangle x y 1 1 [0 0 1 0.6]])
            (when-let [faction show-potential-field-colors?]
              (let [{:keys [distance]} (faction cell*)]
                (when distance
                  (let [ratio (/ distance ((:world/factions-iterations world) faction))]
                    [:draw/filled-rectangle x y 1 1 [ratio (- 1 ratio) ratio 0.6]]))))])))

(defn- draw-image
  [image
   {:keys [entity/body]}
   {:keys [ctx/graphics]}]
  [[:draw/texture-region
    (graphics/texture-region graphics image)
    (:body/position body)
    {:center? true
     :rotation (or (:body/rotation-angle body)
                   0)}]])

(def ^:private hpbar-colors
  {:green     [0 0.8 0 1]
   :darkgreen [0 0.5 0 1]
   :yellow    [0.5 0.5 0 1]
   :red       [0.5 0 0 1]})

(defn- hpbar-color [ratio]
  (let [ratio (float ratio)
        color (cond
               (> ratio 0.75) :green
               (> ratio 0.5)  :darkgreen
               (> ratio 0.25) :yellow
               :else          :red)]
    (color hpbar-colors)))

(def ^:private borders-px 1)

(defn- draw-hpbar [world-unit-scale {:keys [body/position body/width body/height]} ratio]
  (let [[x y] position]
    (let [x (- x (/ width  2))
          y (+ y (/ height 2))
          height (* 5          world-unit-scale)
          border (* borders-px world-unit-scale)]
      [[:draw/filled-rectangle x y width height color/black]
       [:draw/filled-rectangle
        (+ x border)
        (+ y border)
        (- (* width ratio) (* 2 border))
        (- height          (* 2 border))
        (hpbar-color ratio)]])))

(def ^:private skill-image-radius-world-units
  (let [tile-size 48
        image-width 32]
    (/ (/ image-width tile-size) 2)))

(defn- draw-skill-image
  [texture-region entity [x y] action-counter-ratio]
  (let [radius skill-image-radius-world-units
        y (+ (float y)
             (float (/ (:body/height (:entity/body entity)) 2))
             (float 0.15))
        center [x (+ y radius)]]
    [[:draw/filled-circle center radius [1 1 1 0.125]]
     [:draw/sector
      center
      radius
      90 ; start-angle
      (* (float action-counter-ratio) 360) ; degree
      [1 1 1 0.5]]
     [:draw/texture-region texture-region [(- (float x) radius) y]]]))

(def effect-k->fn
  {:effects/target-all {:draw (fn [_
                                   {:keys [effect/source]}
                                   {:keys [ctx/world]}]
                                (let [{:keys [world/active-entities]} world
                                      source* @source]
                                  (for [target* (map deref (target-all/affected-targets active-entities world source*))]
                                    [:draw/line
                                     (:body/position (:entity/body source*)) #_(start-point source* target*)
                                     (:body/position (:entity/body target*))
                                     [1 0 0 0.5]])))}

   :effects/target-entity {:draw (fn [[_ {:keys [maxrange]}]
                                      {:keys [effect/source effect/target]}
                                      _ctx]
                                   (when target
                                     (let [body        (:entity/body @source)
                                           target-body (:entity/body @target)]
                                       [[:draw/line
                                         (target-entity/start-point body target-body)
                                         (target-entity/end-point body target-body maxrange)
                                         (if (target-entity/in-range? body target-body maxrange)
                                           [1 0 0 0.5]
                                           [1 1 0 0.5])]])))}})

(defn draw-effect [{k 0 :as component} effect-ctx ctx]
  (if-let [f (:draw (effect-k->fn k))]
    (f component effect-ctx ctx)
    nil))

(def ^:private render-layers
  (let [outline-alpha 0.4
        enemy-color [1 0 0 outline-alpha]
        friendly-color [0 1 0 outline-alpha]
        neutral-color [1 1 1 outline-alpha]
        mouseover-ellipse-width 5]
    [{:entity/mouseover?     (fn
                               [_
                                {:keys [entity/body
                                        entity/faction]}
                                {:keys [ctx/world]}]
                               (let [player @(:world/player-eid world)]
                                 [[:draw/with-line-width mouseover-ellipse-width
                                   [[:draw/ellipse
                                     (:body/position body)
                                     (/ (:body/width  body) 2)
                                     (/ (:body/height body) 2)
                                     (cond (= faction (faction/enemy (:entity/faction player)))
                                           enemy-color
                                           (= faction (:entity/faction player))
                                           friendly-color
                                           :else
                                           neutral-color)]]]]))

      :stunned               (fn [_ {:keys [entity/body]} _ctx]
                               [[:draw/circle
                                 (:body/position body)
                                 0.5
                                 [1 1 1 0.6]]])

      :player-item-on-cursor (fn
                               [{:keys [item]}
                                entity
                                {:keys [ctx/graphics
                                        ctx/input
                                        ctx/stage]}]
                               ; TODO do not draw here, only at UI view
                               ; then graphics can draw world without stage/input
                               (when (player-item-on-cursor/world-item? (ui/mouseover-actor stage (input/mouse-position input)))
                                 [[:draw/texture-region
                                   (graphics/texture-region graphics (:entity/image item))
                                   (player-item-on-cursor/item-place-position (:graphics/world-mouse-position graphics)
                                                                              entity)
                                   {:center? true}]]))}
     {:entity/clickable      (fn
                               [{:keys [text]}
                                {:keys [entity/body
                                        entity/mouseover?]}
                                _ctx]
                               (when (and mouseover? text)
                                 (let [[x y] (:body/position body)]
                                   [[:draw/text {:text text
                                                 :x x
                                                 :y (+ y (/ (:body/height body) 2))
                                                 :up? true}]])))

      :entity/animation      (fn [animation entity ctx]
                               (draw-image (animation/current-frame animation)
                                           entity
                                           ctx))

      :entity/image          draw-image

      :entity/line-render    (fn [{:keys [thick? end color]} {:keys [entity/body]} _ctx]
                               (let [position (:body/position body)]
                                 (if thick?
                                   [[:draw/with-line-width
                                     4
                                     [[:draw/line position end color]]]]
                                   [[:draw/line position end color]])))}

     {:npc-sleeping          (fn [_ {:keys [entity/body]} _ctx]
                               (let [[x y] (:body/position body)]
                                 [[:draw/text {:text "zzz"
                                               :x x
                                               :y (+ y (/ (:body/height body) 2))
                                               :up? true}]]))

      :entity/temp-modifier  (fn [_ entity _ctx]
                               [[:draw/filled-circle
                                 (:body/position (:entity/body entity))
                                 0.5
                                 [0.5 0.5 0.5 0.4]]])

      :entity/string-effect  (fn [{:keys [text]} entity {:keys [ctx/graphics]}]
                               (let [[x y] (:body/position (:entity/body entity))]
                                 [[:draw/text {:text text
                                               :x x
                                               :y (+ y
                                                     (/ (:body/height (:entity/body entity)) 2)
                                                     (* 5 (:graphics/world-unit-scale graphics)))
                                               :scale 2
                                               :up? true}]]))}

     {:entity/stats          (fn [_ entity {:keys [ctx/graphics]}]
                               (let [ratio (val-max/ratio (stats/get-hitpoints (:entity/stats entity)))]
                                 (when (or (< ratio 1) (:entity/mouseover? entity))
                                   (draw-hpbar (:graphics/world-unit-scale graphics)
                                               (:entity/body entity)
                                               ratio))))
      :active-skill          (fn
                               [{:keys [skill effect-ctx counter]}
                                entity
                                {:keys [ctx/graphics
                                        ctx/world]
                                 :as ctx}]
                               (let [{:keys [entity/image skill/effects]} skill]
                                 (concat (draw-skill-image (graphics/texture-region graphics image)
                                                           entity
                                                           (:body/position (:entity/body entity))
                                                           (timer/ratio (:world/elapsed-time world) counter))
                                         (mapcat #(draw-effect % effect-ctx ctx)  ; update-effect-ctx here too ?
                                                 effects))))}]))

(def ^:dbg-flag show-body-bounds? false)

(defn- draw-body-rect [{:keys [body/position body/width body/height]} color]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    [[:draw/rectangle x y width height color]]))

(defn- draw-entity
  [{:keys [ctx/graphics]
    :as ctx}
   entity render-layer]
  (try (do
        (when show-body-bounds?
          (graphics/draw! graphics (draw-body-rect (:entity/body entity)
                                                   (if (:body/collides? (:entity/body entity))
                                                     color/white
                                                     color/gray))))
        (doseq [[k v] entity
                :let [draw-fn (get render-layer k)]
                :when draw-fn]
          (graphics/draw! graphics (draw-fn v entity ctx))))
       (catch Throwable t
         (graphics/draw! graphics (draw-body-rect (:entity/body entity) color/red))
         (throwable/pretty-pst t))))

(defn draw-entities
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (let [entities (map deref (:world/active-entities world))
        player @(:world/player-eid world)
        should-draw? (fn [entity z-order]
                       (or (= z-order :z-order/effect)
                           (raycaster/line-of-sight? world player entity)))]
    (doseq [[z-order entities] (utils/sort-by-order (group-by (comp :body/z-order :entity/body) entities)
                                                    first
                                                    (:world/render-z-order world))
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (draw-entity ctx entity render-layer))))

(defn- draw-on-world-viewport!
  [{:keys [ctx/graphics]
    :as ctx}]
  (graphics/draw-on-world-vp! graphics
                              (fn []
                                (doseq [f [draw-tile-grid
                                           draw-cell-debug
                                           draw-entities
                                           #_moon.application.render.draw-on-world-viewport.geom-test/do!
                                           highlight-mouseover-tile]]
                                  (graphics/draw! graphics (f ctx)))))
  ctx)

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
   [:interaction-state/mouseover-actor (ui/actor-information stage mouseover-actor)]

   (and mouseover-eid
        (:entity/clickable @mouseover-eid))
   [:interaction-state/clickable-mouseover-eid
    {:clicked-eid mouseover-eid
     :in-click-range? (< (body/distance (:entity/body @player-eid)
                                        (:entity/body @mouseover-eid))
                         (:entity/click-distance-tiles @player-eid))}]

   :else
   (if-let [skill-id (ui/action-bar-selected-skill stage)]
     (let [entity @player-eid
           skill (skill-id (:entity/skills entity))
           effect-ctx (player-effect-ctx mouseover-eid world-mouse-position player-eid)
           state (skill/usable-state skill entity effect-ctx)]
       (if (= state :usable)
         [:interaction-state.skill/usable [skill effect-ctx]]
         [:interaction-state.skill/not-usable state]))
     [:interaction-state/no-skill-selected])))

(defn- assoc-interaction-state
  [{:keys [ctx/graphics
           ctx/input
           ctx/stage
           ctx/world]
    :as ctx}]
  (assoc ctx :ctx/interaction-state (interaction-state stage
                                                       (:graphics/world-mouse-position graphics)
                                                       (:world/mouseover-eid world)
                                                       (:world/player-eid    world)
                                                       (ui/mouseover-actor stage (input/mouse-position input)))))

(defn- player-idle->cursor [player-eid {:keys [ctx/interaction-state]}]
  (let [[k params] interaction-state]
    (case k
      :interaction-state/mouseover-actor
      (let [[actor-type params] params
            inventory-cell-with-item? (and (= actor-type :mouseover-actor/inventory-cell)
                                           (let [inventory-slot params]
                                             (get-in (:entity/inventory @player-eid) inventory-slot)))]
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

(let [fn-map {:active-skill :cursors/sandclock
              :player-dead :cursors/black-x
              :player-idle player-idle->cursor
              :player-item-on-cursor :cursors/hand-grab
              :player-moving :cursors/walking
              :stunned :cursors/denied}]
  (defn state->cursor [[k v] eid ctx]
    (let [->cursor (k fn-map)]
      (if (keyword? ->cursor)
        ->cursor
        (->cursor eid ctx)))))

(defn- set-cursor
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (let [eid (:world/player-eid world)
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-key (state->cursor [state-k (state-k entity)] eid ctx)]
    (graphics/set-cursor! graphics cursor-key))
  ctx)

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
             (ui/inventory-window-visible? stage)
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

(let [fn-map {:player-idle           (fn
                                       [player-eid
                                        {:keys [ctx/input
                                                ctx/interaction-state
                                                ctx/stage] :as ctx}]
                                       (if-let [movement-vector (input/player-movement-vector input)]
                                         [[:tx/event player-eid :movement-input movement-vector]]
                                         (when (input/button-just-pressed? input Input$Buttons/LEFT)
                                           (interaction-state->txs interaction-state
                                                                   stage
                                                                   player-eid))))

              :player-item-on-cursor (fn
                                       [eid {:keys [ctx/input
                                                    ctx/stage]}]
                                       (let [mouseover-actor (ui/mouseover-actor stage (input/mouse-position input))]
                                         (when (and (input/button-just-pressed? input Input$Buttons/LEFT)
                                                    (player-item-on-cursor/world-item? mouseover-actor))
                                           [[:tx/event eid :drop-item]])))

              :player-moving         (let [speed (fn [{:keys [entity/stats]}]
                                                   (or (stats/get-stat-value stats :stats/movement-speed)
                                                       0))]
                                       (fn [eid {:keys [ctx/input]}]
                                         (if-let [movement-vector (input/player-movement-vector input)]
                                           [[:tx/assoc eid :entity/movement {:direction movement-vector
                                                                             :speed (speed @eid)}]]
                                           [[:tx/event eid :no-movement-input]])))}]
  (defn- state->handle-input [[k v] eid ctx]
    (if-let [f (k fn-map)]
      (f eid ctx)
      nil)))

(defn- player-state-handle-input
  [{:keys [ctx/world]
    :as ctx}]
  (let [eid (:world/player-eid world)
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (state->handle-input [state-k (state-k entity)] eid ctx)]
    (handle! ctx txs))
  ctx)

(defn- dissoc-interaction-state [ctx]
  (dissoc ctx :ctx/interaction-state))

(def pausing? true)

(def state->pause-game? {:stunned false
                         :player-moving false
                         :player-item-on-cursor true
                         :player-idle true
                         :player-dead true
                         :active-skill false})

(defn- assoc-paused
  [{:keys [ctx/input
           ctx/world]
    :as ctx}]
  (assoc-in ctx [:ctx/world :world/paused?]
            (or #_error
                (and pausing?
                     (state->pause-game? (:state (:entity/fsm @(:world/player-eid world))))
                     (not (or (input/key-just-pressed? input (:unpause-once input/controls))
                              (input/key-pressed? input (:unpause-continously input/controls))))))))

(defn- update-world-time
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (if (:world/paused? (:ctx/world ctx))
    ctx
    (update ctx :ctx/world world/update-time (graphics/delta-time graphics))))

(defn- update-potential-fields
  [{:keys [ctx/world]
    :as ctx}]
  (if (:world/paused? world)
    ctx
    (do
     (world/update-potential-fields! world)
     ctx)))

(defn- tick-entities
  [{:keys [ctx/skin
           ctx/stage
           ctx/world]
    :as ctx}]
  (if (:world/paused? world)
    ctx
    (do (try
         (handle! ctx (world/tick-entities! world))
         (catch Throwable t
           (throwable/pretty-pst t)
           (ui/show-error-window! stage skin t)))
        ctx)))

(defn- remove-destroyed-entities
  [{:keys [ctx/world]
    :as ctx}]
  (handle! ctx (world/remove-destroyed-entities! world))
  ctx)

(def zoom-speed 0.025)

(defn- window-camera-controls
  [{:keys [ctx/input
           ctx/graphics
           ctx/stage]
    :as ctx}]
  (when (input/key-pressed? input (:zoom-in input/controls))
    (graphics/change-zoom! graphics zoom-speed))

  (when (input/key-pressed? input (:zoom-out input/controls))
    (graphics/change-zoom! graphics (- zoom-speed)))

  (when (input/key-just-pressed? input (:close-windows-key input/controls))
    (ui/close-all-windows! stage))

  (when (input/key-just-pressed? input (:toggle-inventory input/controls))
    (ui/toggle-inventory-visible! stage))

  (when (input/key-just-pressed? input (:toggle-entity-info input/controls))
    (ui/toggle-entity-info-window! stage))
  ctx)

(defn- render-stage
  [{:keys [^Stage ctx/stage]
    :as ctx}]
  (set! (.ctx stage) ctx)
  (.act  stage)
  (.draw stage)
  (.ctx  stage))

(defn render! [ctx]
  (-> ctx
      get-stage-ctx
      validate
      update-mouse
      update-mouseover-eid
      check-open-debug
      assoc-active-entities
      set-camera-on-player
      clear-screen!
      draw-world-map!
      draw-on-world-viewport!
      assoc-interaction-state
      set-cursor
      player-state-handle-input
      dissoc-interaction-state
      assoc-paused
      update-world-time
      update-potential-fields
      tick-entities
      remove-destroyed-entities
      window-camera-controls
      render-stage
      validate))

(def state (atom nil))

; for every ' symbol/name ' give documentation for _ALL_ revelant information and zusammenhaenge
; e.g. a function requires 'txs' as result (entity tick, ..)
;> point to all txs with params and documentation?
(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (let [config (edn-resource "config.edn")]
    (Lwjgl3Application. (reify ApplicationListener
                          (create [_]
                            (reset! state (create!
                                           {:gdx/audio    Gdx/audio
                                            :gdx/files    Gdx/files
                                            :gdx/graphics Gdx/graphics
                                            :gdx/input    Gdx/input}
                                           config)))

                          (dispose [_]
                            (dispose! @state))

                          (render [_]
                            (swap! state render!))

                          (resize [_ width height]
                            (resize! @state width height))

                          (pause [_])

                          (resume [_]))
                        (doto (Lwjgl3ApplicationConfiguration.)
                          (.setTitle (:title config))
                          (.setWindowedMode (:width (:window config))
                                            (:height (:window config)))
                          (.setForegroundFPS (:fps config))))))
