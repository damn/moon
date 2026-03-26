(ns moon.dev-menu.menus.open-editor
  (:require [clojure.string :as str]
            [moon.db :as db]
            [moon.stage :as stage]
            [moon.property-editor-window :as property-editor-window]
            [moon.property-overview-window :as property-overview-window]))

(defn create [{:keys [ctx/db]}]
  {:label "Editor"
   :items (for [property-type (sort (db/property-types db))]
            {:label (str/capitalize (name property-type))
             :on-click (fn [_actor {:keys [ctx/db
                                           ctx/skin
                                           ctx/stage
                                           ctx/textures]}]
                         (stage/add-actor! stage
                                           (property-overview-window/create
                                            {:db db
                                             :textures textures
                                             :skin skin
                                             :property-type property-type
                                             :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                                              (stage/add-actor! stage
                                                                                (property-editor-window/create
                                                                                 {:ctx ctx
                                                                                  :property (db/get-raw db id)})))})))})})
