(ns editor.main-window
  (:require [clojure.string :as str]
            [scene2d.actor.add-listener :refer [add-listener!]]
            [scene2d.event.get-stage :as get-stage]
            [editor.window]
            [scene2d.ui.text-button :as text-button]
            [scene2d.utils.change-listener :as change-listener]
            [gdx.scenes.scene2d.ui.window :as window]
            [scene2d.stage.add-actor :refer [add-actor!]]
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
                             (add-listener! (change-listener/create
                                             (fn [event actor]
                                               (let [{:keys [ctx/db
                                                             ctx/skin
                                                             ctx/stage
                                                             ctx/textures
                                                             ctx/property-overview-window]
                                                      :as ctx} (:stage/ctx (get-stage/f event))]
                                                 (add-actor! stage
                                                             (property-overview-window
                                                              {:db db
                                                               :textures textures
                                                               :skin skin
                                                               :property-type property-type
                                                               :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                                                                (add-actor! stage
                                                                                            (editor.window/property-editor-window
                                                                                             {:ctx ctx
                                                                                              :property (get-raw db id)})))})))))))}])}))
