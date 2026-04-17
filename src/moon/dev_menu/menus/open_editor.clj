(ns moon.dev-menu.menus.open-editor
  (:require [clojure.string :as str]
            [moon.db :as db]
            [clojure.scene2d.actor :as actor]
            [clojure.scene2d.stage :as stage]))

(defn create [{:keys [ctx/db]}]
  {:label "Editor"
   :items (for [property-type (sort (db/property-types db))]
            {:label (str/capitalize (name property-type))
             :on-click (fn [_actor {:keys [ctx/db
                                           ctx/skin
                                           ctx/stage
                                           ctx/textures]
                                    :as ctx}]
                         (stage/add-actor! stage
                                           (actor/create
                                            {:type :ui/property-overview-window
                                             :db db
                                             :textures textures
                                             :skin skin
                                             :property-type property-type
                                             :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                                              (stage/add-actor! stage
                                                                                (actor/create
                                                                                 {:type :ui/property-editor-window
                                                                                  :ctx ctx
                                                                                  :property (db/get-raw db id)})))})))})})
