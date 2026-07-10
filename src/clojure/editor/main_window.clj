(ns clojure.editor.main-window
  (:require
            [clojure.table-set-opts :as table-set-opts]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.editor.create-widget-property-editor-window :refer [property-editor-window]]
            [clojure.editor.property-overview-window :refer [property-overview-window]]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [clojure.db :as db]
            [clojure.property-types :refer [property-types]]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [clojure.string :as str]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]))

(defn f
  [{:keys [ctx/db
           ctx/skin]}]
  (doto (window/new "Edit" skin)
    (table-set-opts/set-opts! {:title "Edit"
    :skin skin
    :table/rows (for [property-type (sort (property-types db))]
                  [{:actor (doto (text-button/new (str/capitalize (name property-type)) skin)
                             (actor/addListener (change-listener/create
                                                      (fn [event _actor]
                                                        (let [{:keys [ctx/db
                                                                      ctx/skin
                                                                      ctx/stage
                                                                      ctx/textures]
                                                               :as ctx} (:stage/ctx (event/getStage event))]
                                                          (stage/addActor stage
                                                                            (property-overview-window
                                                                             {:db db
                                                                              :textures textures
                                                                              :skin skin
                                                                              :property-type property-type
                                                                              :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                                                                                (stage/addActor stage
                                                                                                                  (property-editor-window
                                                                                                                   {:ctx ctx
                                                                                                                    :property (db/get-raw db id)})))})))))))}])})))
