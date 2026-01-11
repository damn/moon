; this is just the create the game step ! ... da haegnt so viel dran ... weird !
; just the function which creates data .. ?
; _how_ it is created not complect with the processing we do on a piece of data
(ns moon.create
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [moon.animation]
            [moon.app :as app]
            [moon.body]
            [moon.ctx :as ctx]
            [moon.db :as db]
            [moon.entity.skills]
            [moon.entity.state :as state]
            [moon.entity.state-impl]
            [moon.entity.stats]
            [moon.graphics :as graphics]
            [moon.inventory]
            [moon.timer :as timer]
            [moon.ui :as ui]
            [moon.ui.editor.widgets-impl]
            [moon.ui.editor.window]
            [moon.ui.stage :as stage]
            [moon.world :as world]
            [moon.world-fns.creature-tiles]
            [moon.world.tiled-map :as tiled-map]
            [qrecord.core :as q])
  (:import (com.badlogic.gdx Audio                          ; we dont require this, but a new 'idea' of audio with just 'new-sound'
                             Files
                             Input)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)))

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

(defn do!
  [app config]
  (let [db (db/create)
        graphics (graphics/create! (app/graphics app) (app/files app) (:graphics config)) ; graphics/sounds/input already part of application?!
        stage (ui/create! graphics)
        skin (create-skin (.internal (app/files app) "uiskin.json"))
        ctx (merge (map->Context {})
                   {:ctx/audio (load-sounds (app/audio app) (app/files app) (:audio config))
                    :ctx/db db
                    :ctx/graphics graphics
                    :ctx/input (app/input app)
                    :ctx/stage stage
                    :ctx/skin skin})]
    (Input/.setInputProcessor (app/input app) stage)
    ; all ui building inside moon.ui ??
    ; just pass game-fns ?
    (doseq [actor (map (fn [sym] ((requiring-resolve sym) ctx)) (:ui/actors config))]
      (stage/add-actor! stage actor))
    ; this form what is input/output?
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
