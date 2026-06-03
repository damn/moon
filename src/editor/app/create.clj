(ns editor.app.create
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.files :as files]
            [clojure.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [clojure.gdx.input :as input]
            [clojure.gdx.scene2d.ui.skin :as skin]
            [clojure.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [editor.main-window :as main-window]
            [clojure.gdx.scene2d.stage :as stage]
            [clojure.gdx.scene2d.stage.add-actor :refer [add-actor!]]
            [gdx.textures]
            [moon.db :as db]))

(defn f! [app _params]
  (let [skin (skin/create (files/internal (app/files app) "skin/uiskin.json"))
        ui-viewport (fit-viewport/create 1440 900)
        batch (sprite-batch/create)
        stage (stage/create ui-viewport batch)
        db (db/create)]
    (input/set-processor! (app/input app) stage)
    (add-actor! stage (main-window/create db skin))
    {:ctx/app app
     :ctx/batch batch
     :ctx/db db
     :ctx/skin skin
     :ctx/stage stage
     :ctx/textures (gdx.textures/create (app/files app))
     :ctx/ui-viewport ui-viewport
     :ctx/property-k-sort-order [:property/id
                                 :property/pretty-name
                                 :entity/image
                                 :entity/animation
                                 :entity/species
                                 :creature/level
                                 :entity/body
                                 :item/slot
                                 :projectile/speed
                                 :projectile/max-range
                                 :projectile/piercing?
                                 :skill/action-time-modifier-key
                                 :skill/action-time
                                 :skill/start-action-sound
                                 :skill/cost
                                 :skill/cooldown]}))
