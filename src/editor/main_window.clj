(ns editor.main-window
  (:require [clojure.stage :as stage]
            [clojure.actor :as actor]
            [clojure.event :as event]
            [clojure.string :as str]
            [editor.window]
            [gdx.scene2d.ui.text-button :as text-button]
            [gdx.scene2d.utils.change-listener :as change-listener]
            [gdx.scene2d.ui.window :as window]
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
                             (actor/add-listener! (change-listener/create
                                              (fn [event actor]
                                                (let [{:keys [ctx/db
                                                              ctx/skin
                                                              ctx/stage
                                                              ctx/textures
                                                              ctx/property-overview-window]
                                                       :as ctx} (:stage/ctx (event/get-stage event))]
                                                  (stage/add-actor! stage
                                                               (property-overview-window
                                                                {:db db
                                                                 :textures textures
                                                                 :skin skin
                                                                 :property-type property-type
                                                                 :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                                                                  (stage/add-actor! stage
                                                                                               (editor.window/property-editor-window
                                                                                                {:ctx ctx
                                                                                                 :property (get-raw db id)})))})))))))}])}))
