(ns clojure.editor.main-window
  (:require [clojure.scene2d.actor.add-listener]
            [clojure.editor.create-widget-property-editor-window :refer [property-editor-window]]
            [clojure.editor.property-overview-window :refer [property-overview-window]]
            [clojure.event :as event]
            [clojure.db.get-raw :refer [get-raw]]
            [clojure.property-types :refer [property-types]]
            [clojure.stage :as stage]
            [clojure.string :as str]
            [clojure.ui-text-button :as text-button]
            [clojure.ui-window :as window]
            [clojure.scene2d.utils.change-listener :as change-listener]))

(defn f
  [{:keys [ctx/db
           ctx/skin]}]
  (window/create
   {:title "Edit"
    :skin skin
    :table/rows (for [property-type (sort (property-types db))]
                  [{:actor (doto (text-button/create
                                  {:text (str/capitalize (name property-type))
                                   :skin skin})
                             (clojure.scene2d.actor.add-listener/f (change-listener/create
                                                      (fn [event _actor]
                                                        (let [{:keys [ctx/db
                                                                      ctx/skin
                                                                      ctx/stage
                                                                      ctx/textures]
                                                               :as ctx} (:stage/ctx (event/get-stage event))]
                                                          (stage/add-actor! stage
                                                                            (property-overview-window
                                                                             {:db db
                                                                              :textures textures
                                                                              :skin skin
                                                                              :property-type property-type
                                                                              :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                                                                                (stage/add-actor! stage
                                                                                                                  (property-editor-window
                                                                                                                   {:ctx ctx
                                                                                                                    :property (get-raw db id)})))})))))))}])}))
