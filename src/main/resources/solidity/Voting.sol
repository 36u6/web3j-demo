pragma solidity ^0.4.24;

contract Voting {

  mapping (bytes32 => uint8) public votes;
  bytes32[] public candidates;

  constructor(bytes32[] candidateNames) public {
    candidates = candidateNames;
  }

  function getVotesFor(bytes32 candidate) view public returns (uint8) {
    require(validCandidate(candidate));
    return votes[candidate];
  }

  function voteFor(bytes32 candidate) public {
    require(validCandidate(candidate));
    votes[candidate]  += 1;
  }

  function validCandidate(bytes32 candidate) view public returns (bool) {
    for(uint i = 0; i < candidates.length; i++) {
      if (candidates[i] == candidate) {
        return true;
      }
    }
    return false;
   }
}