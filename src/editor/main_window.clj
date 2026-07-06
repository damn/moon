(ns editor.main-window
  (:require [clojure.gdx.actor.add-listener :as add-listener]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [clojure.gdx.stage.add-actor :as add-actor]
            [clojure.string :as str]
            [editor.window]
            [scene2d.ui.text-button :as text-button]
            [scene2d.utils.change-listener :as change-listener]
            [gdx.scenes.scene2d.ui.window :as window]
            [moon.db.property-types :refer [property-types]]
            [moon.db.get-raw :refer [get-raw]]))

(defn create
  [{:keys [ctx/db
           ctx/skin]}]
  (window/create
   {:title "Edit"
    :skin skin
    :table/rows (for [property-type (sort (property-types db))]
                  [{:actor (doto (text-button/create
                                  {:text (str/capitalize (name property-type))
                                   :skin skin})
                             (add-listener/f (change-listener/create
                                              (fn [event actor]
                                                (let [{:keys [ctx/db
                                                              ctx/skin
                                                              ctx/stage
                                                              ctx/textures
                                                              ctx/property-overview-window]
                                                       :as ctx} (:stage/ctx (event/get-stage event))]
                                                  (add-actor/f stage
                                                               (property-overview-window
                                                                {:db db
                                                                 :textures textures
                                                                 :skin skin
                                                                 :property-type property-type
                                                                 :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                                                                  (add-actor/f stage
                                                                                               (editor.window/property-editor-window
                                                                                                {:ctx ctx
                                                                                                 :property (get-raw db id)})))})))))))}])}))
