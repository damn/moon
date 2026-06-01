(ns editor.app.create
  (:require [clojure.gdx.application :as app]
            [com.badlogic.gdx.files :as files]
            [clojure.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [com.badlogic.gdx.input :as input]
            [clojure.gdx.scene2d.ui.skin :as skin]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [editor.main-window :as main-window]
            [gdx.stage :as stage]
            [gdx.textures]
            [moon.db :as db]))

(defn f! [app _params]
  (let [skin (skin/create (files/internal (app/files app) "skin/uiskin.json"))
        ui-viewport (fit-viewport/create 1440 900)
        batch (sprite-batch/create)
        stage (stage/create ui-viewport batch)
        db (db/create)]
    (input/set-processor! (app/input app) stage)
    (stage/add-actor! stage (main-window/create db skin))
    {:ctx/app app
     :ctx/batch batch
     :ctx/db db
     :ctx/skin skin
     :ctx/stage stage
     :ctx/textures (gdx.textures/create (app/files app))
     :ctx/ui-viewport ui-viewport}))
