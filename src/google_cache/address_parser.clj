(ns google-cache.address-parser
  (:require [clojure.string :as str :refer [split trim join lower-case replace]])
  (:refer-clojure :exclude [replace]))

(defn parse [address]
  (->> (-> address
           (lower-case)
           (replace "," " ")
           (trim)
           (split #"\s+")
           (sort))
       (join " ")))

; Next idea is to use normalization:
; ""ул. Седова" => "улица Седова", "8к1" => "8 корпус 1" or vise versa.
