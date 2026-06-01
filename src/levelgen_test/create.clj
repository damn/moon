  (ns levelgen-test.create
    (:require [clojure.gdx.application :as app]
              [com.badlogic.gdx.files :as files]
              [clojure.gdx.graphics.color :as color]
              [clojure.gdx.graphics.g2d.sprite-batch :as sprite-batch]
              [com.badlogic.gdx.input :as input]
              [com.badlogic.gdx.scenes.scene2d.event :as event]
              [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]
              [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
              [gdx.graphics.orthographic-camera :as camera]
              [gdx.scenes.scene2d.actor :as actor]
              [gdx.scenes.scene2d.ui.text-button :as text-button]
              [gdx.scenes.scene2d.ui.window :as window]
              [gdx.stage :as stage]
              [gdx.textures]
              [levelgen-test.generate-level :as generate-level]
              [moon.db :as db]
              [schema.malli-form]))

(def initial-level-fn "config/world_fns/uf_caves.edn")

(def level-fns
  ["config/world_fns/vampire.edn"
   "config/world_fns/uf_caves.edn"
   "config/world_fns/modules.edn"])

(def tile-size 48)

(defn- edit-window [skin]
  {:title "Edit"
   :skin skin
   :table/rows (for [level-fn level-fns
                     :let [on-clicked (fn [actor ctx]
                                        (let [stage (actor/stage actor)
                                              new-ctx (generate-level/f ctx level-fn)]
                                          (stage/set-ctx! stage new-ctx)))]]
                 [{:actor (text-button/create
                           {:text (str "Generate " level-fn)
                            :skin skin
                            :actor/listeners {:listener/change (fn [event actor]
                                                                 (on-clicked actor (:stage/ctx (event/stage event))))}})}])})

(defn f!
  [app _params]
  (let [files (app/files app)
        graphics (app/graphics app)
        input (app/input app)
        skin (skin/create (files/internal files "skin/uiskin.json"))
        ui-viewport (fit-viewport/create 1440 900)
        sprite-batch (sprite-batch/create)
        stage (stage/create ui-viewport sprite-batch)
        _  (input/set-processor! input stage)
        tile-size 48
        world-unit-scale (float (/ tile-size))
        ctx {:ctx/stage stage
             :ctx/files files}
        ctx (assoc ctx :ctx/db (db/create))
        ctx (assoc ctx :ctx/textures (gdx.textures/create files))
        world-viewport (let [world-width  (* 1440 world-unit-scale)
                             world-height (* 900  world-unit-scale)]
                         (fit-viewport/create world-width
                                              world-height
                                              (camera/create
                                               {:y-down? false
                                                :world-width world-width
                                                :world-height world-height})))
        ctx (assoc ctx
                   :ctx/input input
                   :ctx/world-viewport world-viewport
                   :ctx/ui-viewport ui-viewport
                   :ctx/camera (:viewport/camera world-viewport)
                   :ctx/color-setter (constantly (color/float-bits [1 1 1 1]))
                   :ctx/zoom-speed 0.1
                   :ctx/camera-movement-speed 1
                   :ctx/sprite-batch sprite-batch
                   :ctx/world-unit-scale world-unit-scale)
        ctx (generate-level/f ctx initial-level-fn)]
    (stage/add-actor! (:ctx/stage ctx) (window/create (edit-window skin)))
    ctx))
