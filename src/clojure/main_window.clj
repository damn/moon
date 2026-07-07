(ns clojure.main-window
  (:require [clojure.stage :as stage]
            [clojure.actor :as actor]
            [clojure.event :as event]
            [clojure.string :as str]
            [clojure.editor-window]
            [clojure.ui-text-button :as text-button]
            [clojure.utils-change-listener :as change-listener]
            [clojure.ui-window :as window]
            [clojure.property-types :refer [property-types]]
            [clojure.get-raw :refer [get-raw]]))

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
                                                                                               (clojure.editor-window/property-editor-window
                                                                                                {:ctx ctx
                                                                                                 :property (get-raw db id)})))})))))))}])}))
