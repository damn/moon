(ns editor.main-window
  (:require [clojure.string :as str]
            [clojure.gdx.scene2d.event :as event]
            [editor.property-overview-window]
            [editor.window]
            [clojure.gdx.scene2d.ui.text-button :as text-button]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [gdx.scenes.scene2d.ui.window :as window]
            [gdx.stage :as stage]
            [gdx.textures]
            [moon.db :as db]))

(defn create [db skin]
  (window/create
   {:title "Edit"
    :skin skin
    :table/rows (for [property-type (sort (db/property-types db))]
                  [{:actor (text-button/create
                            {:text (str/capitalize (name property-type))
                             :skin skin
                             :actor/listeners [(change-listener/create
                                                (fn [event actor]
                                                  (let [{:keys [ctx/db
                                                                ctx/skin
                                                                ctx/stage
                                                                ctx/textures]
                                                         :as ctx} (:stage/ctx (event/stage event))]
                                                    (stage/add-actor! stage
                                                                      (editor.property-overview-window/create
                                                                       {:db db
                                                                        :textures textures
                                                                        :skin skin
                                                                        :property-type property-type
                                                                        :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                                                                         (stage/add-actor! stage
                                                                                                           (editor.window/property-editor-window
                                                                                                            {:ctx ctx
                                                                                                             :property (db/get-raw db id)})))})))))]})}])}))
